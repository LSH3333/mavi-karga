// 비디오 클릭으로 음소거 or 소리켜기 
function toggleMute() {
    var video = document.getElementById("main_video");
    var button = document.getElementById("main_video_btn");
    video.volume = 0.5;
    if (video.muted) {
        video.muted = false;
        button.classList.remove("muted");
    } else {
        video.muted = true;
        button.classList.add("muted");
    }
    button.offsetWidth;
}