function score = calcscore(h, y, M)
	s = sse( h, y, M)

	avg = mean(y);
	rep = ones(size(h, 1), 1);
	ybar = avg(rep, :);
	s0 = sse( ybar, y, M)

	score = 1 - s / s0;
end

