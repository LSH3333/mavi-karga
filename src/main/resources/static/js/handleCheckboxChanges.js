
// 체크박스들중 적어도 하나는 선택 필수


// 색상 체크박스들 감지
function handleClolorCheckboxChange(checkbox) {
    handleCheckboxChange('.color-checkboxes', checkbox);
}
// 사이즈 체크박스들 감지
function handleSizeCheckboxChange(checkbox) {
    handleCheckboxChange('.size-checkboxes', checkbox);
}

function handleCheckboxChange(className, checkbox) {
    const checkboxes = document.querySelectorAll(className + ' input[type="checkbox"]');
    const firstCheckbox = document.querySelector(className + ' input[type="checkbox"]:first-child');
    // 체크박스 체크 해제한 경우
    // 적어도 하나의 체크박스는 체크처리 되있도록 보장함 
    if (!checkbox.checked) {
        // 체크박스들 순회하면서 모든 체크박스가 해제되었는지 검사
        let allUnchecked = true;
        checkboxes.forEach(cb => {
            if (cb.checked) {
                allUnchecked = false;
                return;
            }
        });
        // 모두 체크 해제된 상태라면 첫번째 체크박스 체크 처리 
        if (allUnchecked) {
            firstCheckbox.checked = true;
        }
    }
    // 체크박스 체크한 경우 
    // 첫번째 이외 체크박스 체크한 경우 첫번째 체크박스는 해제시킨다 
    else {
        if (checkbox != firstCheckbox) {
            firstCheckbox.checked = false;
        }
    }

}