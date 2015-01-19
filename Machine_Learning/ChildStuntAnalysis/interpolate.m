function interp = interpolate(t, y, t0)
	N = length(t);
	if N == 1
		interp = y;
	else
		t_expand = [];
		for i = 1:size(y, 2)
			t_expand = [t_expand t];
		end
		sum_y_sum_t = sum(y) * sum(t);
		sum_yt = sum( y .* t_expand );
		square_sum_t = sum(t)^2;
		sum_t_square = sum( t.*t );
		k = (sum_y_sum_t - N * sum_yt ) / ( square_sum_t - N * sum_t_square );
		b = (sum_yt - k * sum_t_square ) / sum(t);
		interp = k * t0 + b;
	end
end

