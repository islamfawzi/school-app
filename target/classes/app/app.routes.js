angular.module('appRoutes', ['ngRoute'])

.config(function($routeProvider, $locationProvider){
    
	$routeProvider

		.when('/', {

			templateUrl: 'views/pages/home.html',
			controller : 'SchoolController',
			controllerAs: 'school'

		})
		.when('/add', {

			templateUrl: 'views/pages/add.html',
			controller: 'addSchoolController',
			controllerAs: 'school'

		})
        .when('/get/:id', {

        	templateUrl: 'views/pages/add.html',
			controller: 'addSchoolController',
			controllerAs: 'school'
			
        }) 
        .otherwise({
            redirectTo: '/'
            });
	
	// configure html5 to get links working on jsfiddle
    $locationProvider.html5Mode(true);

});