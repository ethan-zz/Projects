function s = sse(h, y, M)
	% M = 2x2 inverse of covariance matrix
	a = M(1,1);
	b = M(1,2);
	c = M(2,2);
	% h = m * 2; m samples, 2 estimates; y = m * 2 truths
	err = h - y;
	tmp = [err(:, 1).^2 2*err(:,1).*err(:,2) err(:,2).^2];
	s = sum( tmp * [a;b;c] );
end
