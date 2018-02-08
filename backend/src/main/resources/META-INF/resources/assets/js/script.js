$(function() {

    // Open/Close menu
    $("a.collapse-menu-link").on( "click", function(ev) {
        ev.preventDefault();
        $(this).parents("main").toggleClass("menu-collapsed");
        // Persist menu state in cookie
        Cookies.set('menu-collapsed',$("main").hasClass("menu-collapsed"));
    });

    // Update the menu state from cookie
    if (Cookies.get("menu-collapsed") != undefined
        && Cookies.get("menu-collapsed") == "true"){
        $("main").addClass("menu-collapsed");
    } else {
        $("main").removeClass("menu-collapsed");
    }

    // Open/Close menuS
    $("a.dropdown-toggle").on( "click", function(ev) {
        ev.preventDefault();
        var child = $(this).parent(".dropdown");
        // Close already opened menu
        $(".dropdown.open").each(function() {
            if (child[0] != $(this)[0]) {
                $(this).removeClass("open");
            }
        });
        child.toggleClass("open");

        // register event to close modal on click outsite
        if (child.hasClass("open")){
            $("body").on('click', function (ev) {
                var target = $(ev.target);
                if (!target.is(".dropdown-toggle") && !target.parents().is('.dropdown-toggle')){
                    child.toggleClass("open");
                    $("body").off('click');
                }
            });
        } else {
            $("body").off('click');
        }
    });

    // Configure nProgress for Ajax events
    NProgress.configure({ showSpinner: false });

});