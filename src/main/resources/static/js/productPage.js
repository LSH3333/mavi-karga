// 회원 장바구니 추가 ajax
let submitBtn = document.getElementById('submitBtn');
if (submitBtn != null) {
    submitBtn.addEventListener('click', function (event) {
        event.preventDefault();
        submitForm();
    })

    function submitForm() {
        let formData = new FormData(document.getElementById('formData'))

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/order/products/add", true);

        xhr.onload = function () {
            if (xhr.readyState == XMLHttpRequest.DONE) {
                if (xhr.status == 200) {
                    console.log("order success");
                    // offcanvas ajax 요청 
                    offcanvsCartAjaxRequest();
                    // offcanvas active
                    const bsOffcanvas = new bootstrap.Offcanvas('#offcanvasExample')
                    bsOffcanvas.toggle();
                } else if (xhr.status == 302) {
                    console.log('302')
                }
                else {
                    console.log("order failed");
                }
            }
        }
        xhr.send(formData);
    };
}

// 비회원 장바구니 추가 ajax
let nonUserSubmitBtn = document.getElementById('nonUserSubmitBtn');
if (nonUserSubmitBtn != null) {
    nonUserSubmitBtn.addEventListener('click', function (event) {
        event.preventDefault();
        submitNonUserForm();
    })
}

function submitNonUserForm() {
    let formData = new FormData(document.getElementById('formData'))

    const xhrNonUser = new XMLHttpRequest();
    xhrNonUser.open("POST", "/order/products/add/nonuser", true);

    xhrNonUser.onload = function () {
        if (xhrNonUser.readyState == XMLHttpRequest.DONE) {
            if (xhrNonUser.status == 200) {
                console.log("order success");
                // offcanvas ajax 요청 
                offcanvsCartAjaxRequest();
                // offcanvas active
                const bsOffcanvas = new bootstrap.Offcanvas('#offcanvasExample')
                bsOffcanvas.toggle();
            } else if (xhrNonUser.status == 302) {
                console.log('302')
            }
            else {
                console.log("order failed");
            }
        }
    }
    xhrNonUser.send(formData);
};


// 상품 옵션 선택하지 않았을시 submit 버튼 못 누르도록
function checkSelectedSize() {
    var selectedSize = document.getElementById("size").value;
    // 회원 장바구니 추가 버튼 
    var submitBtn = document.getElementById("submitBtn");
    if (submitBtn != null) {
        submitBtn.disabled = (selectedSize === "-1");
    }
    // 비회원 장바구니 추가 버튼 
    var submitBtnNonUser = document.getElementById('nonUserSubmitBtn');
    if (submitBtnNonUser != null) {
        submitBtnNonUser.disabled = (selectedSize === "-1");
    }
}


// Delivery & returns 버튼 
