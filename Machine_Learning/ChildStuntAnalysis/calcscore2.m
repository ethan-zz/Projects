function score = calcscore2(h, y, M)
	avg = mean(y);
	rep = ones(size(h, 1), 1);
	ybar = avg(rep, :);

	s = sse( h, ybar, M);

	s0 = sse( y, ybar, M) ;

	score = 1 - s / s0;
end

