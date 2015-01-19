function y = nn1(markers, values, tests)
	y = [];
	for i = 1:size(tests, 1)
		diff = bsxfun(@minus, markers, tests(i, :) );
		diff = diff.^2;
		dist = sum(diff, 2);
		[ignore idx] = min(dist);
		y = [y; values(idx, :)];
	end
end

