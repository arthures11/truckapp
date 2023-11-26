var role = '';

$.get("/role", function(data) {
    role = data;
});



$.get("/user", function(data) {
    $("#user").html(role+' '+data.name);
    $(".unauthenticated").hide()
    $(".authenticated").show()
    $("#avatar").attr("src", 'data:image/png;base64,'+data.avatar);
    $("#username").attr("placeholder", data.name);
    $("#email").attr("placeholder", data.email);
    $(".rounded-circle.mb-3.mt-4").attr("src", 'data:image/png;base64,'+data.avatar);
    for(let i=data.notifications.length-1;i>=0;i--){
        var t = `<a class="dropdown-item d-flex align-items-center" href="#">
                                            <div class="me-3">
                                                <div class="bg-warning icon-circle"><i class="fas fa-exclamation-triangle text-white"></i></div>
                                            </div>
                                            <div><span class="small text-gray-500">`+data.notifications[i].date+`</span>
                                                <p>`+data.notifications[i].opis+`</p>
                                            </div>
                                        </a>`

        $('#notifikacje').append(t)
    }
    $('#alertamount').text(data.notifications.length);
});


function deleteAlerts(){

    $.ajax({
        url: "/alerts/removeall",
        type: "DELETE",
        success: function() {
            console.log("BONUS deleted successfully");
            location.reload()
        },
        error: function() {
            console.log("Error deleting user");
        }
    });

}