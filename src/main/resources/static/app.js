const state = {
    products: [],
    cart: new Map(),
    user: null
};

const authGate = document.querySelector("#authGate");
const appShell = document.querySelector("#appShell");
const productGrid = document.querySelector("#productGrid");
const cartItems = document.querySelector("#cartItems");
const cartCount = document.querySelector("#cartCount");
const cartTotal = document.querySelector("#cartTotal");
const orderForm = document.querySelector("#orderForm");
const message = document.querySelector("#message");
const orderList = document.querySelector("#orderList");
const userPanel = document.querySelector("#userPanel");
const authMessage = document.querySelector("#authMessage");
const profileForm = document.querySelector("#profileForm");
const profileMessage = document.querySelector("#profileMessage");
const logoutButton = document.querySelector("#logoutButton");

document.querySelector("#refreshProducts").addEventListener("click", loadProducts);
document.querySelector("#refreshOrders").addEventListener("click", loadOrders);
document.querySelector("#clearCart").addEventListener("click", () => {
    state.cart.clear();
    renderCart();
});

document.querySelector("#loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitAuthForm(event.currentTarget, "/api/auth/login", "Signed in successfully.");
});

document.querySelector("#registerForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitAuthForm(event.currentTarget, "/api/auth/register", "Account created and signed in.");
});

logoutButton.addEventListener("click", async () => {
    await request("/api/auth/logout", { method: "POST" });
    state.user = null;
    state.products = [];
    state.cart.clear();
    renderAuth();
    renderCart();
    productGrid.innerHTML = "";
    renderOrders([]);
});

profileForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage(profileMessage);

    try {
        state.user = await request("/api/auth/profile", {
            method: "PUT",
            body: formToObject(profileForm)
        });
        renderAuth();
        showMessage(profileMessage, "Shipping profile saved.");
    } catch (error) {
        showMessage(profileMessage, error.message, true);
    }
});

orderForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage(message);

    if (!state.user.profileComplete) {
        showMessage(message, "Please save your shipping profile first.", true);
        return;
    }

    if (state.cart.size === 0) {
        showMessage(message, "Please add products to the cart first.", true);
        return;
    }

    try {
        const result = await request("/api/orders", {
            method: "POST",
            body: {
                items: [...state.cart.entries()].map(([productId, quantity]) => ({ productId, quantity }))
            }
        });

        state.cart.clear();
        showMessage(message, `Order #${result.id} created. Total ¥${money(result.totalAmount)}.`);
        await loadProducts();
        await loadOrders();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

async function submitAuthForm(form, url, successText) {
    clearMessage(authMessage);
    try {
        state.user = await request(url, {
            method: "POST",
            body: formToObject(form)
        });
        form.reset();
        renderAuth();
        showMessage(authMessage, successText);
        await loadProducts();
        await loadOrders();
    } catch (error) {
        showMessage(authMessage, error.message, true);
    }
}

async function loadCurrentUser() {
    state.user = await request("/api/auth/me");
    renderAuth();
    if (state.user) {
        await loadProducts();
        await loadOrders();
    }
}

async function loadProducts() {
    productGrid.innerHTML = "<p class=\"empty\">Loading products...</p>";
    state.products = await request("/api/products");
    renderProducts();
    renderCart();
}

async function loadOrders() {
    orderList.innerHTML = "<p class=\"empty\">Loading orders...</p>";
    const orders = await request("/api/orders");
    renderOrders(orders);
}

function renderAuth() {
    const loggedIn = Boolean(state.user);
    authGate.classList.toggle("hidden", loggedIn);
    appShell.classList.toggle("hidden", !loggedIn);

    if (!loggedIn) {
        profileForm.reset();
        userPanel.innerHTML = "";
        return;
    }

    userPanel.innerHTML = `
        <strong>${escapeHtml(state.user.username)}</strong>
        <span>Account: ${escapeHtml(state.user.account)}</span>
        <span>${state.user.profileComplete ? "Shipping profile complete" : "Shipping profile required"}</span>
    `;
    profileForm.elements.recipientName.value = state.user.recipientName || "";
    profileForm.elements.phone.value = state.user.phone || "";
    profileForm.elements.address.value = state.user.address || "";
}

function renderProducts() {
    productGrid.innerHTML = state.products.map(product => `
        <article class="product-card">
            <img src="${product.imageUrl}" alt="${escapeHtml(product.name)}">
            <div class="product-body">
                <h2>${escapeHtml(product.name)}</h2>
                <p>${escapeHtml(product.description)}</p>
                <div class="product-meta">
                    <span class="price">¥${money(product.price)}</span>
                    <span class="stock">Stock ${product.stock}</span>
                </div>
                <button type="button" onclick="addToCart(${product.id})" ${product.stock <= 0 ? "disabled" : ""}>
                    Add to Cart
                </button>
            </div>
        </article>
    `).join("");
}

function renderCart() {
    const entries = [...state.cart.entries()]
        .map(([productId, quantity]) => ({
            product: state.products.find(item => item.id === productId),
            quantity
        }))
        .filter(item => item.product);

    const totalQuantity = entries.reduce((sum, item) => sum + item.quantity, 0);
    const totalAmount = entries.reduce((sum, item) => sum + Number(item.product.price) * item.quantity, 0);
    cartCount.textContent = `${totalQuantity} items`;
    cartTotal.textContent = `¥${money(totalAmount)}`;

    if (entries.length === 0) {
        cartItems.className = "cart-items empty";
        cartItems.textContent = "Cart is empty";
        return;
    }

    cartItems.className = "cart-items";
    cartItems.innerHTML = entries.map(({ product, quantity }) => `
        <div class="cart-row">
            <div>
                <strong>${escapeHtml(product.name)}</strong>
                <span>¥${money(product.price)} x ${quantity}</span>
            </div>
            <div class="cart-actions">
                <button type="button" onclick="changeQuantity(${product.id}, -1)">-</button>
                <button type="button" onclick="changeQuantity(${product.id}, 1)">+</button>
            </div>
        </div>
    `).join("");
}

function renderOrders(orders) {
    if (!orders.length) {
        orderList.className = "orders empty";
        orderList.textContent = "No orders yet";
        return;
    }

    orderList.className = "orders";
    orderList.innerHTML = orders.map(order => `
        <article class="order-row">
            <strong>#${order.id} ${escapeHtml(order.customerName)}</strong>
            <span>${order.items.length} product types · ¥${money(order.totalAmount)}</span>
            <small>${escapeHtml(order.address)}</small>
        </article>
    `).join("");
}

function addToCart(productId) {
    changeQuantity(productId, 1);
}

function changeQuantity(productId, delta) {
    const product = state.products.find(item => item.id === productId);
    const currentQuantity = state.cart.get(productId) || 0;
    const nextQuantity = currentQuantity + delta;

    if (nextQuantity <= 0) {
        state.cart.delete(productId);
    } else if (product && nextQuantity <= product.stock) {
        state.cart.set(productId, nextQuantity);
    } else {
        showMessage(message, "Cannot exceed current stock.", true);
    }

    renderCart();
}

async function request(url, options = {}) {
    const config = { ...options };
    if (config.body && typeof config.body !== "string") {
        config.headers = { "Content-Type": "application/json", ...(config.headers || {}) };
        config.body = JSON.stringify(config.body);
    }

    const response = await fetch(url, config);
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.message || "Request failed.");
    }

    return data;
}

function formToObject(form) {
    return Object.fromEntries([...new FormData(form).entries()].map(([key, value]) => [key, value.trim()]));
}

function money(value) {
    return Number(value).toFixed(2);
}

function showMessage(target, text, isError = false) {
    target.textContent = text;
    target.classList.toggle("error", isError);
}

function clearMessage(target) {
    showMessage(target, "");
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

loadCurrentUser().catch(error => showMessage(authMessage, error.message, true));
