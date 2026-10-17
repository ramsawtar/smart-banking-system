// ============================================================
//   SmartBank — Common JavaScript Utilities  (Phase 8)
// ============================================================

const API_BASE = '/api';

// ── Auth State ──────────────────────────────────────────────
function getToken()  { return localStorage.getItem('smart_bank_token'); }

function getUser() {
    const s = localStorage.getItem('smart_bank_user');
    return s ? JSON.parse(s) : null;
}

function setAuth(data) {
    localStorage.setItem('smart_bank_token', data.token);
    localStorage.setItem('smart_bank_user', JSON.stringify({
        id:    data.userId,
        name:  data.name,
        email: data.email,
        role:  data.role
    }));
}

function clearAuth() {
    localStorage.removeItem('smart_bank_token');
    localStorage.removeItem('smart_bank_user');
    localStorage.removeItem('smart_bank_active_account');
}

function isAuthenticated() { return !!getToken(); }
function isAdmin()         { const u = getUser(); return u && u.role === 'ADMIN'; }

function logout() {
    clearAuth();
    window.location.href = 'login.html';
}

function requireAuth() {
    if (!isAuthenticated()) window.location.href = 'login.html';
}

function requireAdmin() {
    if (!isAuthenticated()) { window.location.href = 'login.html'; return; }
    if (!isAdmin())          window.location.href = 'dashboard.html';
}

// ── Authenticated Fetch ──────────────────────────────────────
async function authFetch(url, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    try {
        const response = await fetch(url, { ...options, headers });
        if (response.status === 401) {
            clearAuth();
            window.location.href = 'login.html';
            throw new Error('Session expired. Please log in again.');
        }
        return response;
    } catch (err) {
        throw err;
    }
}

// ── Formatters ──────────────────────────────────────────────
function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '₹0.00';
    return '₹' + Number(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

function formatDateShort(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-IN', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
}

// ── Alerts / Toast ───────────────────────────────────────────
function showAlert(message, type = 'success', timeout = 4500) {
    let container = document.getElementById('alertContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'alertContainer';
        document.body.appendChild(container);
    }

    const icon = type === 'success' ? 'bi-check-circle-fill text-success'
               : type === 'warning' ? 'bi-exclamation-circle-fill text-warning'
               : 'bi-x-circle-fill text-danger';

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="bi ${icon} me-2 fs-5 flex-shrink-0"></i>
            <div class="flex-grow-1">${message}</div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    container.appendChild(alertDiv);

    if (timeout > 0) {
        setTimeout(() => {
            alertDiv.classList.remove('show');
            setTimeout(() => alertDiv.remove(), 300);
        }, timeout);
    }
}

// ── Loading Skeleton Rows ────────────────────────────────────
function skeletonRows(cols = 5, count = 4) {
    return Array.from({ length: count }, () =>
        `<tr>${Array.from({ length: cols }, () =>
            `<td><div class="skeleton" style="width:${60 + Math.random()*30|0}%; height:14px;"></div></td>`
        ).join('')}</tr>`
    ).join('');
}

function skeletonCards(count = 4) {
    return Array.from({ length: count }, () =>
        `<div class="skeleton-row"></div>`
    ).join('');
}

// ── Button Loading State ─────────────────────────────────────
function setButtonLoading(btn, loading, originalText = '') {
    if (loading) {
        btn.disabled = true;
        btn._originalHTML = btn.innerHTML;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Processing...`;
    } else {
        btn.disabled = false;
        btn.innerHTML = btn._originalHTML || originalText;
    }
}

// ── Transaction Type Helpers ─────────────────────────────────
function txBadgeHTML(type) {
    const map = {
        DEPOSIT:      { label: 'Deposit',      cls: 'badge-deposit'      },
        WITHDRAW:     { label: 'Withdraw',     cls: 'badge-withdraw'     },
        TRANSFER_OUT: { label: 'Transfer Out', cls: 'badge-transfer_out' },
        TRANSFER_IN:  { label: 'Transfer In',  cls: 'badge-transfer_in'  },
    };
    const t = map[type?.toUpperCase()] || { label: type || '-', cls: 'bg-secondary text-white' };
    return `<span class="${t.cls} px-2 py-1 rounded small fw-semibold">${t.label}</span>`;
}

function txAmountHTML(type, amount) {
    const plus = ['DEPOSIT', 'TRANSFER_IN'].includes(type?.toUpperCase());
    const cls  = plus ? 'tx-amount-plus' : 'tx-amount-minus';
    const sign = plus ? '+' : '-';
    return `<span class="${cls}">${sign} ${formatCurrency(amount)}</span>`;
}

// ── Account Status Badge ─────────────────────────────────────
function statusBadge(status) {
    const s = (status || '').toUpperCase();
    if (s === 'ACTIVE')  return `<span class="badge bg-success-subtle text-success border border-success-subtle"><span class="status-dot status-dot-active"></span>Active</span>`;
    if (s === 'BLOCKED') return `<span class="badge bg-danger-subtle text-danger border border-danger-subtle"><span class="status-dot status-dot-blocked"></span>Blocked</span>`;
    return `<span class="badge bg-secondary-subtle text-secondary">${s}</span>`;
}

// ── Dynamic Navigation Bar ────────────────────────────────────
function renderNavbar(activePage = '') {
    const navbarContainer = document.getElementById('navbarContainer');
    if (!navbarContainer) return;

    const authenticated = isAuthenticated();
    const user          = getUser();
    const userIsAdmin   = isAdmin();

    const customerLinks = [
        { page: 'dashboard',     href: 'dashboard.html',     icon: 'bi-speedometer2',      label: 'Dashboard' },
        { page: 'accounts',      href: 'accounts.html',      icon: 'bi-wallet2',           label: 'Accounts' },
        { page: 'deposit',       href: 'deposit.html',       icon: 'bi-arrow-down-circle', label: 'Deposit' },
        { page: 'withdraw',      href: 'withdraw.html',      icon: 'bi-arrow-up-circle',   label: 'Withdraw' },
        { page: 'transfer',      href: 'transfer.html',      icon: 'bi-arrow-left-right',  label: 'Transfer' },
        { page: 'beneficiaries', href: 'beneficiaries.html', icon: 'bi-person-lines-fill', label: 'Beneficiaries' },
        { page: 'transactions',  href: 'transactions.html',  icon: 'bi-clock-history',     label: 'Transactions' },
    ];

    const adminLinks = [
        { page: 'admin-dashboard', href: 'admin-dashboard.html', icon: 'bi-speedometer2', label: 'Admin Dashboard' },
        { page: 'users',           href: 'users.html',           icon: 'bi-people',       label: 'Customers' },
    ];

    const links = userIsAdmin ? adminLinks : customerLinks;

    const navLinks = authenticated
        ? links.map(l => `
            <li class="nav-item">
                <a class="nav-link ${activePage === l.page ? 'active' : ''}" href="${l.href}">
                    <i class="bi ${l.icon} me-1"></i>${l.label}
                </a>
            </li>`).join('')
        : '';

    let userMenu = '';
    if (authenticated) {
        userMenu = `
            <div class="d-flex align-items-center">
                <div class="dropdown">
                    <button class="btn btn-outline-light dropdown-toggle btn-sm px-3" type="button"
                            id="userMenuBtn" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-person-circle me-1"></i>${user?.name || 'My Account'}
                        <span class="badge ${userIsAdmin ? 'bg-danger' : 'bg-info text-dark'} ms-1">
                            ${user?.role || 'CUSTOMER'}
                        </span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 py-2" aria-labelledby="userMenuBtn"
                        style="border-radius:12px; min-width:200px;">
                        <li class="px-3 py-1">
                            <small class="text-muted d-block fw-semibold">${user?.email || ''}</small>
                        </li>
                        <li><hr class="dropdown-divider my-1"></li>
                        ${!userIsAdmin ? `
                        <li>
                            <a class="dropdown-item rounded-1" href="profile.html">
                                <i class="bi bi-person me-2 text-primary"></i>Profile &amp; Password
                            </a>
                        </li>` : ''}
                        <li>
                            <a class="dropdown-item rounded-1 text-danger" href="javascript:void(0)" onclick="logout()">
                                <i class="bi bi-box-arrow-right me-2"></i>Logout
                            </a>
                        </li>
                    </ul>
                </div>
            </div>`;
    } else {
        userMenu = `
            <div class="d-flex gap-2">
                <a href="login.html" class="btn btn-outline-light btn-sm px-3">Login</a>
                <a href="register.html" class="btn btn-primary btn-sm px-3">Register</a>
            </div>`;
    }

    navbarContainer.innerHTML = `
        <nav class="navbar navbar-expand-lg navbar-dark navbar-bank py-2">
            <div class="container-fluid px-4">
                <a class="navbar-brand d-flex align-items-center"
                   href="${authenticated ? (userIsAdmin ? 'admin-dashboard.html' : 'dashboard.html') : 'index.html'}">
                    <i class="bi bi-bank fs-3 me-2"></i>
                    <span>Smart<span style="color:var(--navy-accent);">Bank</span></span>
                </a>
                <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarContent" aria-controls="navbarContent"
                        aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarContent">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-2">${navLinks}</ul>
                    ${userMenu}
                </div>
            </div>
        </nav>`;
}

// ── Dynamic Footer ────────────────────────────────────────────
function renderFooter() {
    const footerContainer = document.getElementById('footerContainer');
    if (!footerContainer) return;
    footerContainer.innerHTML = `
        <footer class="mt-auto">
            <div class="container text-center">
                <p class="mb-1 text-muted">
                    &copy; ${new Date().getFullYear()} SmartBank &mdash; Banking &amp; Financial Management System
                </p>
                <small class="text-muted">
                    <i class="bi bi-cpu me-1"></i>Java 26 &nbsp;|&nbsp;
                    <i class="bi bi-server me-1"></i>Spring Boot 4.1 &nbsp;|&nbsp;
                    <i class="bi bi-database me-1"></i>MySQL 8 &nbsp;|&nbsp;
                    <i class="bi bi-shield-check me-1"></i>JWT Secured
                </small>
            </div>
        </footer>`;
}

document.addEventListener('DOMContentLoaded', () => {
    renderFooter();
});
