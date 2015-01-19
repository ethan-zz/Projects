function y = nnk(markers, values, tests, k)
	y = [];
	for i = 1:size(tests, 1)
		diff = bsxfun(@minus, markers, tests(i, :) );
		diff = diff.^2;
		dist = sum(diff, 2);
		[ds is] = sort( dist );
		cut = k * k * ds(1); % neighbor boundary cut
		for ns = 2:length(ds)
			if ds( ns ) > cut
				break;
			end
		end
		% Soft max
		w = exp( ds(1) - ds(1:ns) );
		y = [y; w' * values(1:ns, :) / sum(w)];
	end
end

