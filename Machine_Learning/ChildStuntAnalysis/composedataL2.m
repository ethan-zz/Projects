function [idm1, m1, tm1, idm2, m2, tm2, idf1, f1, tf1, idf2, f2, tf2, M] = composedataL2(csv, highorder)
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
	
	% Take the first entry
	data = raw(idx_firstEntry, :);
	features = [2 5:12]; results = [13:14];
	male = find( data(:,3) == 0 ); 
	female = find( data(:, 3) == 1 );
	datam = data( male, : );
	dataf = data( female, : );
	s1 = find(datam(:, 4) == 1 );
	s2 = find(datam(:, 4) == 2 );
	m1 = datam( s1, features ); tm1 = datam( s1, results ); idm1 = datam( s1, 1);
	m2 = datam( s2, features ); tm2 = datam( s2, results ); idm2 = datam( s2, 1);
	s1 = find(dataf(:, 4) == 1 );
	s2 = find(dataf(:, 4) == 2 );
	f1 = dataf( s1, features ); tf1 = dataf( s1, results ); idf1 = dataf( s1, 1);
	f2 = dataf( s2, features ); tf2 = dataf( s2, results ); idf2 = dataf( s2, 1);

	if highorder
		m1 = secondorder( m1 );	 m2 = secondorder( m2 );
		f1 = secondorder( f1 );	 f2 = secondorder( f2 );
	end
end

