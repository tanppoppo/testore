document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".edit-comment").forEach(function(editButton) {
        editButton.addEventListener("click", function(event) {
            event.preventDefault();

            const commentId = this.getAttribute("data-comment-id");
            const commentContentDiv = document.getElementById(`comment-content-${commentId}`);

            if (document.getElementById(`edit-content-${commentId}`)) {
                return;
            }

            const currentContent = commentContentDiv.textContent.trim();
            const textareaHtml = `
                <textarea class="w-full border border-gray-300 p-4 rounded-lg" id="edit-content-${commentId}">${currentContent}</textarea>
                <div class="text-right">
                  <button class="mt-2 p-2 bg-gray-500 text-white rounded text-xs" onclick="saveComment(${commentId})">저장하기</button>
                </div>
                `;
            commentContentDiv.innerHTML = textareaHtml;
        });
    });
});

async function saveComment(commentId) {
    const confirmed = await showModal("정말로 이 댓글을 수정하시겠습니까?", true);

    if (confirmed) {
        const editedContent = document.getElementById(`edit-content-${commentId}`).value;

        const requestData = {
            commentId: commentId,
            content: editedContent
        };

        fetch('/board/updateComment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    throw new Error('댓글 수정에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });

    }
}

async function deleteComment(commentId) {
    const confirmed = await showModal("정말로 이 댓글을 삭제하시겠습니까?", true);

    if (confirmed) {
        fetch('/board/deleteComment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ commentId: commentId })
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    throw new Error('댓글 삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
}