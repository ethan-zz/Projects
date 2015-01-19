function composedataML(csv)
	raw = load( csv );
	[ids, ia, ic] = unique( raw(:, 1) );
	truth = raw( ia, (13:14) );
	%length(truth)
	S = cov( truth ); 
	M = inv(S);
	%corr = corr( truth )

	% Take the first entry
	idx_firstEntry = [1; ia(1:end-1) + 1];
	firstTime = raw(idx_firstEntry, 2);
	firstMeas = raw(idx_firstEntry, 5);
	%ic_size = size(ic)
	fillers = [firstTime(ic) firstMeas(ic)];
	idx_data = [];
	idxjunk = [];
	for i = 1:length(ia)
		if (ia(i) - idx_firstEntry(i)) > 3
			idx_data = [idx_data (idx_firstEntry(i) + 1):ia(i)];
		else
			idxjunk = [idxjunk (idx_firstEntry(i) + 1):ia(i)];
		end
	end
	idx_data = idx_data';
	% data = first time and value added to the other entries data
	data = [raw(idx_data, 1) fillers(idx_data, :) raw(idx_data, 2:4) raw(idx_data, 6:14)];
	features = [1:4 7:15];
	
	male = find( data(:,5) == 0 ); 
	female = find( data(:, 5) == 1 );
	datam = data( male, : );
	dataf = data( female, : );
	s1 = find(datam(:, 6) == 1 );
	s2 = find(datam(:, 6) == 2 );
	m1 = datam( s1, features ); 
	m2 = datam( s2, features ); 
	s1 = find(dataf(:, 6) == 1 );
	s2 = find(dataf(:, 6) == 2 );
	f1 = dataf( s1, features );
	f2 = dataf( s2, features );
	m1 = interp5(m1);	csvwrite('MLm1.csv',m1 );
	m2 = interp5(m2);	csvwrite('MLm2.csv',m2 );
	f1 = interp5(f1);	csvwrite('MLf1.csv',f1 );
	f2 = interp5(f2);	csvwrite('MLf2.csv',f2 );

	%csvwrite('ML.csv', [m1;m2;f1;f2] );
	dataleft =  [raw(idxjunk, 1) fillers(idxjunk, :) raw(idxjunk, 2:4) raw(idxjunk, 6:14) ];
	junks = interp5s( dataleft );
	features = [1:14 17:24 27:34 37:44 47:55];
	csvwrite('MLleft.csv', junks(:, features)  );
end

