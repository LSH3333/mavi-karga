// cart.html, cartNonUser.html

// 사용자가 상품 갯수 변경할때마다 최종 가격 업데이트해서 보여줌
// 제외 버튼 누르면 해당 행 안보이도록 처리 
document.addEventListener('DOMContentLoaded', function () {
    const submitBtn = document.getElementById('submitBtn')
    var rows = document.querySelectorAll('tr');
    // 장바구니 상품 갯수 
    var numberOfCartProduct = rows.length;

    rows.forEach(function (row) {
        // 개당 가격 
        var priceCell = row.querySelector('.price');
        // 갯수 
        var countInput = row.querySelector('.count');
        // 최종 가격
        var totalPriceCell = row.querySelector('.totalPrice');
        // 최종 가격
        var excludeBtn = row.querySelector('.excludeBtn');
        // deleted 값
        // var deletedCheckbox = row.querySelector('.deleted');

        // 상품 가격 * 갯수 = 총 가격 
        function updateTotalPrice() {
            var price = parseFloat(priceCell.textContent);
            var count = parseInt(countInput.value);

            var totalPrice = price * count;
            
            // 한국 원화 
            totalPriceCell.textContent = new Intl.NumberFormat("ko-KR", { style: "currency", currency: "KRW" }).format(
                totalPrice,
            )
        }

        // 제외 버튼 누르면 해당 row 안보이도록 처리 
        function excludeProduct() {
            // 안보이도록 처리 
            row.style.display = 'none';
            // 서버에 보낼 데이터
            // deletedCheckbox.checked = true;
            // 장바구니 상품 갯수 -1
            numberOfCartProduct--;
            // 장바구니 상품 갯수가 0이되면 구매 버튼 클릭 불가능하도록 
            if (numberOfCartProduct === 0) {
                submitBtn.disabled = true;
            }
        }

        // count input 이벤트 리스너 
        countInput.addEventListener('input', updateTotalPrice);

        // 제외 버튼 이벤트 리스너 
        excludeBtn.addEventListener('click', excludeProduct);

        // 가격 업데이트 
        updateTotalPrice();
    });
});
