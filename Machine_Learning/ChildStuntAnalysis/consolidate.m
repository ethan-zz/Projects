function [idout yout tout] = consolidate(idin, yin, target)
	[idout, ia, ic] = unique( idin );
	idx_firstEntry = [1; ia(1:end-1) + 1];
	yout = []; tout = target(ia, :);
	for i = 1:length(ia)
		if idx_firstEntry(i) == ia(i)
			yout = [yout; yin(ia(i), :)];
		else
			%yout = [yout; median( yin(idx_firstEntry(i):ia(i), :) )];
			yout = [yout; mean( yin(idx_firstEntry(i):ia(i), :) )];
		end
	end
end

