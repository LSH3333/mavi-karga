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

// offcanvas 상품 갯수 input 갯수 조절 
$(document).on('click', '.number-spinner button', function () {
    var btn = $(this),
        input = btn.closest('.number-spinner').find('input'),
        cartId = input.data('cartid'),
        count = input.val().trim(),
        newVal = 0;

    if (btn.attr('data-dir') == 'up') {
        newVal = parseInt(count) + 1;
    } else {
        if (count > 1) {
            newVal = parseInt(count) - 1;
        } else {
            newVal = 1;
        }
    }
    input.val(newVal);

    // 상품 갯수 변경 ajax 요청 
    offcanvasChangeProductCount(cartId, newVal);
});


// offcanvas 장바구니 렌더링 ajax 요청 
function offcanvsCartAjaxRequest() {

    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/order/products/cart', true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                //// 서버에서 보내온 장바구니 데이터로 html 엘레먼트 생성 

                // offcanvas body container 내부 초기화 
                document.getElementById('offcanvas-body-container').innerHTML = "";

                // offcanvas scroll container
                var offcanvasScrollContainer = document.createElement('div');
                offcanvasScrollContainer.id = 'offcanvas-scroll-container';
                offcanvasScrollContainer.style.width = '100%';
                offcanvasScrollContainer.classList.add('offcanvas-scroll', 'offcanvas-scroll2');


                data.forEach(function (dto) {
                    let cartId = dto.cartId;
                    let productId = dto.productId;
                    let name = dto.name;
                    let price = dto.price;
                    let thumbnail_url = dto.thumbnail_url;
                    let count = dto.count;
                    let size = dto.size;


                    // row
                    var row = document.createElement('div');
                    row.classList.add('row', 'product-row');

                    //// left element 
                    var leftElement = document.createElement('div');
                    leftElement.classList.add('product-col-left', 'col-4', 'col-lg-4')

                    // 썸네일 
                    var imgAnchor = document.createElement('a');
                    imgAnchor.href = '/order/products?productId=' + productId;
                    var img = document.createElement('img');
                    img.classList.add('img-fluid');
                    img.src = thumbnail_url;
                    img.alt = 'product thumbnail img';

                    imgAnchor.appendChild(img);
                    leftElement.appendChild(imgAnchor);

                    //// right element
                    var rightElement = document.createElement('div');
                    rightElement.classList.add('product-col-right', 'col-8', 'col-lg-8')

                    // name
                    var nameElement = document.createElement('p')
                    nameElement.textContent = name;
                    rightElement.appendChild(nameElement);

                    // size
                    var sizeElement = document.createElement('p')
                    sizeElement.textContent = size;
                    rightElement.appendChild(sizeElement)

                    // price 
                    var priceElement = document.createElement('p')
                    priceElement.textContent = '₩ ' + price;
                    rightElement.appendChild(priceElement)

                    // count input container 
                    var countInputContainer = createCountInputContainerElement(count, cartId);
                    rightElement.appendChild(countInputContainer);

                    // remove btn 
                    var button = document.createElement('button');
                    button.className = 'excludeBtn';
                    button.value = cartId;
                    button.onclick = function () {
                        removeBtnBehavior(this.value);
                    };
                    var u = document.createElement('u');
                    u.textContent = 'REMOVE';
                    button.appendChild(u);
                    rightElement.appendChild(button);

                    row.appendChild(leftElement);
                    row.appendChild(rightElement);

                    offcanvasScrollContainer.appendChild(row);
                })

                document.getElementById('offcanvas-body-container').appendChild(offcanvasScrollContainer)

                // 전체 가격 
                calculateTotalPrice();
            } else {
                // console.error(xhr.responseText);
                alert('invalid cart. try again.')
            }
        }

    };
    xhr.send();
}

// offcanavs 상품 갯수 input element 생성 
function createCountInputContainerElement(count, cartId) {
    var parentDiv = document.createElement('div');
    parentDiv.className = 'count-input-container border border-3';

    var rowDiv = document.createElement('div');
    rowDiv.className = 'row';

    var colDiv = document.createElement('div');
    colDiv.className = 'col-xs-3 col-xs-offset-3';

    var inputGroupDiv = document.createElement('div');
    inputGroupDiv.className = 'input-group number-spinner';

    var span1 = document.createElement('span');
    span1.className = 'input-group-btn';

    var button1 = document.createElement('button');
    button1.className = 'btn btn-default';
    button1.setAttribute('data-dir', 'dwn');

    var i1 = document.createElement('i');
    i1.className = 'fa-solid fa-minus';

    button1.appendChild(i1);

    span1.appendChild(button1);

    var input = document.createElement('input');
    input.className = 'form-control text-center';
    input.setAttribute('style', 'border: none;');
    input.setAttribute('value', count);
    input.setAttribute('data-cartid', cartId);

    var span2 = document.createElement('span');
    span2.className = 'input-group-btn';

    var button2 = document.createElement('button');
    button2.className = 'btn btn-default';
    button2.setAttribute('data-dir', 'up');

    var i2 = document.createElement('i');
    i2.className = 'fa-solid fa-plus';

    button2.appendChild(i2);

    span2.appendChild(button2);

    inputGroupDiv.appendChild(span1);
    inputGroupDiv.appendChild(input);
    inputGroupDiv.appendChild(span2);

    colDiv.appendChild(inputGroupDiv);

    rowDiv.appendChild(colDiv);

    parentDiv.appendChild(rowDiv);

    document.body.appendChild(parentDiv);

    return parentDiv;
}

// offcanvas 장바구니 에서 상품 제거, 서버에 제거 요청
function removeBtnBehavior(cartId) {
    ajaxLoadingSpinnerToggle(true);
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/order/products/cart/remove?cartId=' + cartId, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                // 장바구니 물품 제거 성공 
                console.log('장바구니 물품 제거 성공')
                offcanvsCartAjaxRequest();
            } else {
                // console.error(xhr.responseText);
                alert('invalid cart. try again.')
            }
        }
        ajaxLoadingSpinnerToggle(false);
    };
    xhr.send();
}

// offcanvas 에서 상품 갯수 조절, 서버에 갯수 수정 요청 
function offcanvasChangeProductCount(cartId, count) {
    ajaxLoadingSpinnerToggle(true);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/order/products/cart/count?cartId=' + cartId + '&count=' + count, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                console.log('장바구니 물품 갯수 변경 성공')
                offcanvsCartAjaxRequest();
            } else {
                console.error(xhr.responseText);
                alert('invalid cart. try again.')
            }
        }
        ajaxLoadingSpinnerToggle(false);
    };
    xhr.send();
}

// ajax loading spinner 보여주기, 안보여주기 
// on==true 면 보여줌
function ajaxLoadingSpinnerToggle(on) {
    if (on) {
        // ajax 요청중 보여줄 로딩 스피너
        document.getElementById("spinner-div").style.display = "block";
    } else {
        // ajax 요청 끝났으니 스피너 안보이도록 함 
        document.getElementById("spinner-div").style.display = "none";
    }
}

// 전체 가격 계산, 랜더링 
function calculateTotalPrice() {
    let totalPrice = 0;
    // product-row 순회하면서 총액 계산 
    $('.product-row').each(function () {
        var price = $(this).find('.product-col-right p:nth-child(3)').text().trim();
        price = price.replace('₩', '').trim();
        var count = $(this).find('.count-input-container input').val().trim();
        var rowPrice = parseInt(price) * parseInt(count);
        totalPrice += rowPrice;
    });
    // 총 가격 
    document.getElementById('total-price').innerHTML = '₩ ' + totalPrice + '<br>Tax included';
}