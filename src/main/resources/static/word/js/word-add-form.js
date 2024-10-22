document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
       event.preventDefault();

       try {
           const text1 = document.getElementById('text1').value;
           if (!text1 || text1.length < 1 || text1.length > 10) {
               await window.showModal("한자는 1자 이상 10자 이하로 <br>입력하세요.", false);
               return false;
           }

           const text2 = document.getElementById('text2').value;
           if (!text2 || text2.length < 1 || text2.length > 30) {
               await window.showModal("요미가나는 1자 이상 30자 이하로 <br>입력하세요.", false);
               return false;
           }

           const text3 = document.getElementById('text3').value;
           if (!text3 || text3.length < 1 || text3.length > 10) {
               await window.showModal("한글은 1자 이상 10자 이하로 <br>입력하세요", false);
               return false;
           }

           document.getElementById('wordForm').submit();

       } catch (error) {
           console.error("유효성 검사 도중 오류가 발생했습니다 : ", error);
       }
    })
})