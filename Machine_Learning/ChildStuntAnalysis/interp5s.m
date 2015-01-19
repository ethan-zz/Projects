function dout = interp5s( din )
	[ids ia ic] = unique(din(:, 1) );
	begins = [1; ia(1:end-1) + 1];
	sizes = ia - begins + 1;
	N = size(din,2);
	lastData = N-2;
	idxw = N-1;
	dout = [];
	for i = 1:length(ia)
		me = din( begins(i):ia(i), : );
		if sizes(i) == 1
			tmp = [me; me; me; me; me];
			me = tmp;
		else if ( sizes(i) == 2 )
			tmp = me(1, :);
			dy = ( me(2, :) - me(1, :) ) / 4;
			for k = 1:3
				tmp = [tmp; me(1, :) + dy * k];
			end
			tmp = [tmp; me(2, :)];
			me = tmp;
		else % size is 3
			tmp = me(1, :);
			tmp = [tmp; (me(1,:) + me(2,:))/2; me(2, :)];
			tmp = [tmp; (me(2, :) + me(3, :))/2; me(3, :)];
			me = tmp;
		end

		combined = me(1, 1:lastData);
		for i = 2:5
			combined = [combined me(i, 4:lastData) ];
		end
		combined = [combined me(1, idxw:N)];
		dout = [dout; combined];
	end
end

