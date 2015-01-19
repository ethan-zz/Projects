function  composedataMLi(csv)
	raw = load( csv );
	[ids, ia, ic] = unique( raw(:, 1) );
	truth = raw( ia, (13:14) );
	%length(truth)
	S = cov( truth ); 
	M = inv(S);
	%corr = corr( truth )

	idx_firstEntry = [1; ia(1:end-1) + 1];
	for i = 1:length(ia)
		idx0 = idx_firstEntry(i);
		t0 = raw(idx0, 2);
		t =  raw( (idx0 + 1):ia(i), 2);
		y = raw( (idx0 + 1):ia(i), 6:12);
		y0 = interpolate( t, y, t0);
		raw( idx0, 6:12 ) = y0;
	end
	features = [1:2 5:14];
	% Take the first entry
	data = raw(idx_firstEntry, :);
	male = find( data(:,3) == 0 ); 
	female = find( data(:, 3) == 1 );
	datam = data( male, : );
	dataf = data( female, : );
	s1 = find(datam(:, 4) == 1 );
	s2 = find(datam(:, 4) == 2 );
	m1 = datam( s1, features ); 
	m2 = datam( s2, features ); 
	s1 = find(dataf(:, 4) == 1 );
	s2 = find(dataf(:, 4) == 2 );
	f1 = dataf( s1, features );
	f2 = dataf( s2, features );

	csvwrite('MLm1.csv',m1 );
	csvwrite('MLm2.csv',m2 );
	csvwrite('MLf1.csv',f1 );
	csvwrite('MLf2.csv',f2 );
end

