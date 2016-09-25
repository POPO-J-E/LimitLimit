function loadScript(url, callback = null)
{
    // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    if(callback)
    {  
        script.onreadystatechange = callback;
        script.onload = callback;
    }

    // Fire the loading
    head.appendChild(script);
}

var onLoadingJsLoaded = function() 
{
    $('.container-login').removeClass('hidden');
    var login = new LoginLayer('.input-username', '.input-login')
};

$( document ).ready(function() {
    onLoadingJsLoaded();
});