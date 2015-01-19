function [J gradient] = costwd(X, t, theta, lambda)
	% X = m * (n + 1); m samples, n features
	m = size(X, 1);
	% theta = (n+1);
	y = X * theta; % y = m * 1; weight and date predication
	err = y - t;
	J = ( err' * err + lambda * sum( theta(2:end).^2) )/m/2;
	gradient = ( X' * err + lambda * [0; theta(2:end)] )/m;
end
