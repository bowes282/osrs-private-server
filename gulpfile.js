var gulp = require('gulp');

gulp.task('default', function() {
	console.log("Currently there are no available tasks. There will be!");
});

/**
 * Builds the client files into a jar and moves it to the 'build/client' folder.
 */
/*
gulp.task('build-client', function() {
	return gulp.src('.')
		.pipe() // Build source/client/bin and data/client into a JAR.
		.pipe(gulp.dest('build/client'));
});
*/

/**
 * Builds the server files into a jar and moves it to the 'build/client' folder.
 */
/*
gulp.task('build-server', function() {
	return gulp.src('.')
		.pipe() // Build source/server/bin and data/server into a JAR.
		.pipe(gulp.dest('build/server'));
});
*/