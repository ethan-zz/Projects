function dout = interp5( din )
	[ids ia ic] = unique(din(:, 1) );
	begins = [1; ia(1:end-1) + 1];
	sizes = ia - begins + 1;
	N = size(din,2);
	lastData = N-2;
	idxw = N-1;
	dout = [];
	for i = 1:length(ia)
		me = din( begins(i):ia(i), : );
		if ( sizes(i) == 4 )
			t = me( :, 4 );
			y = me( :, 5:lastData );
			t0 = (t(2) + t(3))/2;
			interp = interpolate( t, y, t0);
			me = [me(1:2,:); [me(1, 1:3) t0 interp me(1, (idxw:N))]; me(3:4, :)];
		else if ( sizes(i) == 6 )
			t = me( :, 4 );
			y = me( :, 5:lastData );
			t0 = (t(3) + t(4))/2;
			interp = interpolate( t, y, t0);
			me = [me(1:2,:); [me(1, 1:3) t0 interp me(1, (idxw:N))]; me(5:6, :)];
		else
			;
		end

		combined = me(1, 1:lastData);
		for i = 2:5
			combined = [combined me(i, 4:lastData) ];
		end
		combined = [combined me(1, idxw:N)];
		dout = [dout; combined];
	end
end

