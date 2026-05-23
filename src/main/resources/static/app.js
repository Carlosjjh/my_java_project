const state = {
    products: [],
    cart: new Map()
};

const productGrid = document.querySelector("#productGrid");
const cartItems = document.querySelector("#cartItems");
const cartCount = document.querySelector("#cartCount");
const cartTotal = document.querySelector("#cartTotal");
const orderForm = document.querySelector("#orderForm");
const message = document.querySelector("#message");
const orderList = document.querySelector("#orderList");

document.querySelector("#refreshProducts").addEventListener("click", loadProducts);
document.querySelector("#refreshOrders").addEventListener("click", loadOrders);
document.querySelector("#clearCart").addEventListener("click", () => {
    state.cart.clear();
    renderCart();
});

orderForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage();

    if (state.cart.size === 0) {
        showMessage("请先添加商品到购物车", true);
        return;
    }

    const formData = new FormData(orderForm);
    const payload = {
        customerName: formData.get("customerName").trim(),
        phone: formData.get("phone").trim(),
        address: formData.get("address").trim(),
        items: [...state.cart.entries()].map(([productId, quantity]) => ({ productId, quantity }))
    };

    try {
        const response = await fetch("/api/orders", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.message || "提交订单失败");
        }

        state.cart.clear();
        orderForm.reset();
        showMessage(`订单 #${result.id} 创建成功，金额 ¥${money(result.totalAmount)}`);
        await loadProducts();
        await loadOrders();
    } catch (error) {
        showMessage(error.message, true);
    }
});

async function loadProducts() {
    productGrid.innerHTML = "<p class=\"empty\">正在加载商品...</p>";
    const response = await fetch("/api/products");
    state.products = await response.json();
    renderProducts();
    renderCart();
}

async function loadOrders() {
    orderList.innerHTML = "<p class=\"empty\">正在加载订单...</p>";
    const response = await fetch("/api/orders");
    const orders = await response.json();
    if (orders.length === 0) {
        orderList.className = "orders empty";
        orderList.textContent = "暂无订单";
        return;
    }

    orderList.className = "orders";
    orderList.innerHTML = orders
        .slice()
        .reverse()
        .map(order => `
            <article class="order-row">
                <strong>#${order.id} ${escapeHtml(order.customerName)}</strong>
                <span>${order.items.length} 类商品 · ¥${money(order.totalAmount)}</span>
            </article>
        `)
        .join("");
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
                    <span class="stock">库存 ${product.stock}</span>
                </div>
                <button type="button" onclick="addToCart(${product.id})" ${product.stock <= 0 ? "disabled" : ""}>
                    加入购物车
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
    cartCount.textContent = `${totalQuantity} 件商品`;
    cartTotal.textContent = `¥${money(totalAmount)}`;

    if (entries.length === 0) {
        cartItems.className = "cart-items empty";
        cartItems.textContent = "购物车为空";
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
        showMessage("不能超过当前库存", true);
    }

    renderCart();
}

function money(value) {
    return Number(value).toFixed(2);
}

function showMessage(text, isError = false) {
    message.textContent = text;
    message.classList.toggle("error", isError);
}

function clearMessage() {
    showMessage("");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

loadProducts();
loadOrders();
