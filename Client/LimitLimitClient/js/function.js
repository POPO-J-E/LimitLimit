$(document).ready(function(){

	var target = $('#DragAndDrop');

	$('.whiteCard').dblclick(function(){
		if(target.hasClass("empty")){
			$(this).appendTo("#DragAndDrop");
			target.removeClass("empty");
			target.addClass("full");
		}
		else{
			var switched = target.find(".whiteCard");
			switched.appendTo("#whitehand");
			$(this).appendTo("#DragAndDrop");
		}
	})

	var ttt = document.getElementsByClassName("whiteCard");

	for (i=0;i<ttt.length;i++){
		ttt[i].addEventListener("touchstart", tapHandler);
    }

	var tapedTwice = false;

	function tapHandler(event) {
		if(!tapedTwice) {
			tapedTwice = true;
			setTimeout( function() { tapedTwice = false; }, 300 );
			return false;
		}
		event.preventDefault();
		//action on double tap goes below
		if(target.hasClass("empty")){
			$(this).appendTo("#DragAndDrop");
			target.removeClass("empty");
			target.addClass("full");
		}
		else{
			var switched = target.find(".whiteCard");
			switched.appendTo("#whitehand");
			$(this).appendTo("#DragAndDrop");
		}
	}

	$( "#Menu" ).click(function() {
	  $('#usersContainer').addClass('show');
	  $('.fa-bars').hide();
	});

	$( "#Close" ).click(function() {
	  $('#usersContainer').removeClass('show');
	   $('.fa-bars').show();
	});

});

