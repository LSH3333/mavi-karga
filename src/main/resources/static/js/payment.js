// 페이지 로드 시 함수 호출
document.addEventListener('DOMContentLoaded', function () {
    calculateSubtotal(); // subtotal 계산 함수 
    modifyTextContent(); // 모바일 화면에서 total, count 등 텍스트 표시 
});

//////////////////////////////////
// subtotal 계산 
function calculateSubtotal() {
    // 'productTotal' 클래스 이름을 가진 모든 요소 가져오기
    var productTotalElements = document.querySelectorAll('.productTotal');

    // 총액 변수 초기화
    var subtotal = 0;

    // 각 'productTotal' 요소를 반복
    productTotalElements.forEach(function (productTotalElement) {
        // 요소의 텍스트 내용을 가져와서 통화 기호 제거
        var productTotalText = productTotalElement.textContent.replace('₩ ', '');

        // 텍스트 내용을 숫자로 변환하고 총액에 더하기
        subtotal += parseFloat(productTotalText);
    });

    // 총액 요소의 텍스트 내용을 계산된 총액으로 설정
    document.getElementById('subtotal').textContent = '₩ ' + subtotal;
}


//////////////////////////////////
// 화면 너비 줄어들면 order-product-table 의 각 행의 가격,갯수,토탈 텍스트 표시 
// 텍스트가 추가되었는지 여부를 추적하는 플래그
var textAdded = false;

// 화면 너비가 768px 미만인지 확인하는 함수
function isMobile() {
    return window.innerWidth < 768;
}

// 화면 너비에 따라 요소의 텍스트 내용을 수정하는 함수
function modifyTextContent() {
    var productPrices = document.querySelectorAll('.productPrice');
    var productCounts = document.querySelectorAll('.productCount');
    var productTotals = document.querySelectorAll('.productTotal');

    if (isMobile() && !textAdded) {
        productPrices.forEach(function (productPrice) {
            productPrice.textContent = 'price: ' + productPrice.textContent;
        });
        productCounts.forEach(function (productCount) {
            productCount.textContent = 'count: ' + productCount.textContent;
        });
        productTotals.forEach(function (productTotal) {
            productTotal.textContent = 'total: ' + productTotal.textContent;
        });

        // 텍스트가 추가되었음을 나타내는 플래그를 true로 설정
        textAdded = true;
    } else if (!isMobile()) {
        productPrices.forEach(function (productPrice) {
            productPrice.textContent = productPrice.textContent.replace('price: ', '');
        });
        productCounts.forEach(function (productCount) {
            productCount.textContent = productCount.textContent.replace('count: ', '');
        });
        productTotals.forEach(function (productTotal) {
            productTotal.textContent = productTotal.textContent.replace('total: ', '');
        });

        // 텍스트가 제거되었음을 나타내는 플래그를 false로 설정
        textAdded = false;
    }
}

// 화면 크기가 변경될 때 modifyTextContent 함수 호출
window.addEventListener('resize', modifyTextContent);



///////////////////////////////////////
// input 의 모든 required 필드 입력하지 않으면 submit 버튼 클릭해도 다음으로 넘어가지 않도록 함
document.getElementById("paymentForm").addEventListener("submit", function (event) {
    // Get all required input elements in the form
    var requiredInputs = document.querySelectorAll('input[required]');

    // Flag to track whether all required fields are filled
    var allFieldsFilled = true;

    // Check each required input field
    requiredInputs.forEach(function (input) {
        if (input.value.trim() === '') {
            allFieldsFilled = false;
        }
    });

    // If any required field is empty, prevent form submission
    if (!allFieldsFilled) {
        // alert("Please fill in all required fields.");
        event.preventDefault(); // Prevents the form from being submitted                
    }
    else {
        event.preventDefault();
        // 모든 required 필드 입력됐다면 결재창 띄움
        // requestStoreUserInputInfo("/payments/validate/nonuser");  // 비회원 
        requestStoreUserInputInfo("/payments/validate");  // 회원 
    }
});



//////////////////////////////////////////
// bootstrap form validation
// Example starter JavaScript for disabling form submissions if there are invalid fields
(() => {
    'use strict'

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll('.needs-validation')

    // Loop over them and prevent submission
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }

            form.classList.add('was-validated')
        }, false)
    })
})()