function [idm1, m1, tm1, idm2, m2, tm2, idf1, f1, tf1, idf2, f2, tf2, M] = composedataL(csv, highorder)
	raw = load( csv );
	[ids, ia, ic] = unique( raw(:, 1) );
	truth = raw( ia, (13:14) );
	%length(truth)
	S = cov( truth ); 
	M = inv(S);
	%corr = corr( truth )

	data = [];
	% Take the first entry of each ID
	idx_firstEntry = [1; ia(1:end-1) + 1];
	for i = 1:length(ia)
		idx0 = idx_firstEntry(i);
		me = raw(idx0, 1:5);
		for idx_data = idx0 : 
		data = [data; me];
	end
	idx_data = idx_data';
	% data = first time and value added to the other entries data
	data = [raw(idx_data, 1) fillers(idx_data, :) raw(idx_data, 2:4) raw(idx_data, 6:14)];
	features = [2:4 7:13]; results = [14:15];
	male = find( data(:,5) == 0 ); 
	female = find( data(:, 5) == 1 );
	datam = data( male, : );
	dataf = data( female, : );
	s1 = find(datam(:, 6) == 1 );
	s2 = find(datam(:, 6) == 2 );
	m1 = datam( s1, features ); tm1 = datam( s1, results ); idm1 = datam( s1, 1);
	m2 = datam( s2, features ); tm2 = datam( s2, results ); idm2 = datam( s2, 1);
	s1 = find(dataf(:, 6) == 1 );
	s2 = find(dataf(:, 6) == 2 );
	f1 = dataf( s1, features ); tf1 = dataf( s1, results ); idf1 = dataf( s1, 1);
	f2 = dataf( s2, features ); tf2 = dataf( s2, results ); idf2 = dataf( s2, 1);

	if highorder
		m1 = secondorder( m1 );	 m2 = secondorder( m2 );
		f1 = secondorder( f1 );	 f2 = secondorder( f2 );
	end
end

