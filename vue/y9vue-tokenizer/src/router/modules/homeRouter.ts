const homeRouter = {
    // path: "/home",
    // component: () => import("@/layouts/index.vue"),
    // redirect: "/home",
	// name:"homeIndex",
    // meta: {
    // 	title: "首页",
    // 	icon: "ri-home-8-line"
    // },
    // children: [
    	// {
    		path: "/home",
    		component: () => import("@/views/home/index.vue"),
    		name: "home",
    		meta: { title: "首页", icon: "ri-home-8-line" },
    	// },
		
    // ]
};

export default homeRouter;