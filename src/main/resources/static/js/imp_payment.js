
let buyer_name_req;
let buyer_tel_req;
let buyer_addr_req;
let buyer_postcode_req;
let buyer_email_req;

const merchant_uid = crypto.randomUUID();

var IMP = window.IMP;
IMP.init("imp18574515"); // 가맹점 식별코드 

function requestStoreUserInputInfo(payment_server_req_path) {
    const formData = new FormData();
    // // 포트원 결재 정보 
    formData.append("merchant_uid", merchant_uid);  // 제품 번호 
    // 입력받은 사용자 정보 
    formData.append("name", document.getElementById('name').value);
    formData.append("email", document.getElementById('email').value);
    formData.append("phone", document.getElementById('phone').value);
    // 배송 주소 
    formData.append("postcode", document.getElementById('postcode').value);
    formData.append("roadAddress", document.getElementById('roadAddress').value);
    formData.append("jibunAddress", document.getElementById('jibunAddress').value);
    formData.append("detailAddress", document.getElementById('detailAddress').value);
    formData.append("extraAddress", document.getElementById('extraAddress').value);

    const payReqXML = new XMLHttpRequest();
    payReqXML.open("POST", payment_server_req_path, true);
    payReqXML.onload = function () {
        if (payReqXML.status === 200) {
            console.log("사용자 정보 데이터 서버 전송 성공");
            var orderLookUpNumber = payReqXML.responseText;
            
            requestPay(orderLookUpNumber)
            console.log('orderLookUpNumber = ' + orderLookUpNumber)
        } else {
            console.log("사용자 정보 데이터 서버 전송 실패")
            window.location.href = '/payments/paymentFail';
        }
    }
    payReqXML.send(formData);
}

// 원포트에 결재 요청 api 
function requestPay(orderLookUpNumber) {
    console.log('requestPay')
    IMP.request_pay(
        {
            // pg: "kakaopay",
            pg: 'html5_inicis', // KG이니시스
            pay_method: "card",
            merchant_uid: merchant_uid, // 제품 번호 
            name: name_req,
            amount: amount_req,
            buyer_email: buyer_email_req,
            buyer_name: buyer_name_req,
            buyer_tel: buyer_tel_req,
            buyer_addr: buyer_addr_req,
            buyer_postcode: buyer_postcode_req,
        },
        function (rsp) { // 결과 
            // 결재 요청 성공 -> 서버에서 검증 
            if (rsp.success) {
                // const formData = new FormData();
                // // 포트원 결재 정보 
                // formData.append("imp_uid", rsp.imp_uid);
                // formData.append("paid_amount", rsp.paid_amount);
                // formData.append("merchant_uid", rsp.merchant_uid);

                // // 입력받은 사용자 정보 
                // formData.append("name", document.getElementById('name').value);
                // formData.append("email", document.getElementById('email').value);
                // formData.append("phone", document.getElementById('phone').value);
                // // 배송 주소 
                // formData.append("postcode", document.getElementById('postcode').value);
                // formData.append("roadAddress", document.getElementById('roadAddress').value);
                // formData.append("jibunAddress", document.getElementById('jibunAddress').value);
                // formData.append("detailAddress", document.getElementById('detailAddress').value);
                // formData.append("extraAddress", document.getElementById('extraAddress').value);

                // // console.log('==============')
                // // console.log('pg_provider = ' + rsp.pg_provider)
                // // console.log('paid_at = ' + rsp.paid_at);
                // // console.log('rsp.name = ' + rsp.name)
                // // console.log('rsp.pay_method = ' + rsp.pay_method)
                // // console.log('rsp.buyer_email = ' + rsp.buyer_email)   
                // // console.log('rsp.buyer_tel = ' + rsp.buyer_tel)             


                // // formData.append("pay_method", rsp.pay_method);
                // // formData.append("name", rsp.name); // 주문자명 
                // // formData.append("pg_provider ", rsp.pg_provider); // PG사 구분코드
                // // formData.append("buyer_email", rsp.buyer_email);
                // // formData.append("buyer_tel", rsp.buyer_tel);
                // // formData.append("paid_at", rsp.paid_at);

                // const payReqXML = new XMLHttpRequest();
                // payReqXML.open("POST", payment_server_req_path, true);
                // payReqXML.onload = function () {
                //     if (payReqXML.status === 200) {
                //         var orderLookUpNumber = payReqXML.responseText;
                //         console.log("결재 요청 데이터 서버 전송 성공");
                //         // console.log('orderLookUpNumber = ' + orderLookUpNumber)
                //         window.location.href = '/payments/paymentSuccess?orderLookUpNumber=' + orderLookUpNumber;
                //     } else {
                //         console.log("결재 요청 데이터 서버 전송 실패")
                //         // window.location.href = '/payments/paymentFail';
                //         cancelPayments(rsp);
                //     }
                // }
                // payReqXML.send(formData);
                console.log('결제 요청 성공')
                console.log('orderLookUpNumber = ' + orderLookUpNumber)
                // 결제 성공 페이지로 이동 
                window.location.href = '/payments/paymentSuccess?orderLookUpNumber=' + orderLookUpNumber;
            }
            // 결재 요청 실패 
            else {
                alert("결제에 실패하였습니다. 에러 내용: " + rsp.error_msg);
                // 결재 실패 시 결재 환불 
                cancelPayments(rsp);
            }
        }
    );
}

// 환불
function cancelPayments(temp) {
    // console.log('cancelPayments()')

    let data = null;

    if (temp != null) {
        console.log('결재 금액이 달라졌을 때 취소 ' + temp.paid_amount)
        // 결제 금액이 달라졌을 때 결제 취소
        data = {
            impUid: temp.imp_uid,
            reason: "결제 금액 위/변조. 결제 금액이 일치 안 함",
            checksum: temp.paid_amount,
            refundHolder: temp.buyer_name,
            refund_bank: "우리은행"
        };
    } else {
        // todo: 유저가 환불을 요청했을 때 데이터

    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/payments/cancel", true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

    xhr.onload = function () {
        if (xhr.status === 200) {
            alert("결제 금액 환불 완료");
            // self.close(); // 팝업 창 닫기
            // 결제 취소 화면으로 이동해주기.
            window.location.href = '/payments/paymentFail';
        } else {
            alert("결제 금액 환불 못함. 이유: " + xhr.responseText);
            window.location.href = '/payments/paymentFail';
        }
    };

    xhr.onerror = function () {
        alert("Error occurred during the request.");
    };

    xhr.send(JSON.stringify(data));
}



