// admin/add.html 에서 사용

// <input type="file"> 에서 사용자가 이미지 선택하면 여기에 저장됨 
let selectedFiles = [];

// input 에서 받은 파일 리스트에 저장 
function saveSelectedImagesToList() {
    let input = document.getElementById('fileInput');

    for (var i = 0; i < input.files.length; i++) {
        selectedFiles.push(input.files[i]);
    }

    renderImagesInContainer();
}

// 리스트에 담겨있는 이미지들 랜더링 
function renderImagesInContainer() {
    let imageContainer = document.getElementById('imageContainer');
    imageContainer.innerHTML = '';

    selectedFiles.forEach(function (file, index) {
        console.log(file);
        let img = document.createElement('img');
        img.src = URL.createObjectURL(file);
        img.style.width = '100px';
        img.style.margin = '5px';


        let anchor = document.createElement('a');
        anchor.href = '#';
        anchor.appendChild(img);

        anchor.addEventListener('click', function (event) {
            event.preventDefault(); // href='#' 이벤트 제외 
            // 클릭시 이미지 제거 
            removeFileFromList(index);
        });

        imageContainer.appendChild(anchor);
    });
}

// 리스트에서 이미지 제거 
function removeFileFromList(index) {
    if (index >= 0 && index < selectedFiles.length) {
        selectedFiles.splice(index, 1);
        renderImagesInContainer();
    }
}

// 서버에 이미지 파일들 전송 
function uploadImages(productId) {
    // 리스트 -> formData 로 
    let formData = new FormData();
    selectedFiles.forEach(function (file, index) {
        formData.append('multipartFiles', file);
    });

    // AJAX
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/admins/products/images?productId=' + productId, true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) { // request complete 
            if (xhr.status === 200) { // request success
                console.log('success uploadImages')
                // navigated 될 경로 담김                         
                const newPath = xhr.responseText;
                window.location.href = newPath;
            } else { // request fail
                console.log('fail uploadImages')
            }
        }
    };
    xhr.send(formData);
}

// 폼 전송 
function submitForm() {
    const formData = new FormData(document.getElementById('productForm'));

    // AJAX
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/admins/products/add', true);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) { // request complete 
            if (xhr.status === 200) { // request success
                console.log('success submitForm')
                const productId = xhr.responseText;
                uploadImages(productId); // 이미지 전송 
            } else { // request fail
                console.log('fail submitForm')
                // 에러 내용 담김
                const errorResult = xhr.responseText;
                // 에러 알림 
                alert(errorResult);
            }
        }
    };
    xhr.send(formData);
}
