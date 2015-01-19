function newdata = secondorder(data)
	newdata = data;
	cols = size(data, 2);
	for col = 1:cols
		newdata = [newdata data(:, 1:(cols - col + 1)) .* data(:, col:cols)];
	end
end

