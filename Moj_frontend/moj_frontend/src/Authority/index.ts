import router from "@/router";

import { UserStore } from "@/store/user";

router.beforeEach(async (to, from, next) => {
    const user = UserStore();

    if (!user.isLoggedIn) {
        await user.fetchCurrentUser();
    }

    const req = to.meta;
    console.log(req);
    if (req?.target === 'admin') {
        if (!(user.isLoggedIn && user.loginUser?.userRole === 'admin')) {
            next('/noAuthView');
        }
    } else if (req?.target === 'user') {
        if (!(user.isLoggedIn)) {
            next('/noAuthView');
        }
    } 

    next();
})
