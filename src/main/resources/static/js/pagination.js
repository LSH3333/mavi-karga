// Pagination javascript 


// 현재 페이지 
let pageNum = Number("[[${page}]]")
// 전체 페이지 
let totalpages = Number("[[${orderList.totalPages}]]")

const pagination_li_1 = document.getElementById('pagination-li-1');
const pagination_li_2 = document.getElementById('pagination-li-2');
const pagination_li_3 = document.getElementById('pagination-li-3');
const pagination_a_1 = document.getElementById('pagination-a-1')
const pagination_a_2 = document.getElementById('pagination-a-2')
const pagination_a_3 = document.getElementById('pagination-a-3')
const pagination_prev = document.getElementById('pagination-prev');
const pagination_next = document.getElementById('pagination-next')
const pagination_prev_a = document.getElementById('pagination-prev-a')
const pagination_next_a = document.getElementById('pagination-next-a')
const liList = [null, pagination_li_1, pagination_li_2, pagination_li_3]
const aList = [null, pagination_a_1, pagination_a_2, pagination_a_3]

// console.log('pageNum, totalPages = ' + pageNum, totalpages);

// prev, next 버튼 href 설정 
pagination_prev_a.href = '?page=' + (Math.max(pageNum - 4, 0)).toString();
pagination_next_a.href = '?page=' + (Math.min(pageNum + 2, totalpages - 1)).toString();

// totalPages 3개 이하일 경우 그만큼만 랜더링 
if (totalpages <= 3) {
    // 1~totalPages 숫자 할당 
    for (let i = 1; i <= totalpages; i++) {
        aList[i].text = (i).toString();
        aList[i].href = '?page=' + (i - 1).toString();
    }
    // 이외에는 안보이도록 display:none 처리 
    for (let i = totalpages + 1; i <= 3; i++) {
        liList[i].style.display = 'none';
    }
    // 현재 페이지 표시 
    liList[pageNum].classList.add('active')
    pagination_prev.classList.add('disabled')
    pagination_next.classList.add('disabled')
}
// totalPages 4개 이상일 경우 
else {

    if (pageNum < 2) {
        pagination_a_1.text = '1';
        pagination_a_2.text = '2';
        pagination_a_3.text = '3';

        pagination_a_1.href = '?page=0';
        pagination_a_2.href = '?page=1';
        pagination_a_3.href = '?page=2';

        pagination_li_1.classList.add('active') // 현재 페이지 표시
        pagination_prev.classList.add('disabled') // 이전 버튼 disabled
    }
    else if (pageNum === totalpages) {
        pagination_a_1.text = (pageNum - 2).toString();
        pagination_a_2.text = (pageNum - 1).toString();
        pagination_a_3.text = (pageNum).toString();

        pagination_a_1.href = '?page=' + (pageNum - 2 - 1).toString();
        pagination_a_2.href = '?page=' + (pageNum - 1 - 1).toString();
        pagination_a_3.href = '?page=' + (pageNum - 1).toString();

        pagination_li_3.classList.add('active')
        pagination_next.classList.add('disabled')
    }
    else {
        pagination_a_1.text = (pageNum - 1).toString();
        pagination_a_2.text = (pageNum).toString();
        pagination_a_3.text = (pageNum + 1).toString();

        pagination_a_1.href = '?page=' + (pageNum - 1 - 1).toString();
        pagination_a_2.href = '?page=' + (pageNum - 1).toString();
        pagination_a_3.href = '?page=' + (pageNum + 1 - 1).toString();

        pagination_li_2.classList.add('active')
    }
}