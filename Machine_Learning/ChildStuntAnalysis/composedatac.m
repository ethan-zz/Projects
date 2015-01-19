function [idm1, m1, tm1, idm2, m2, tm2, idf1, f1, tf1, idf2, f2, tf2, M, mus, sigmas] = composedatac(csv, cin, stdin, calcFactor)
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
	for i = 1:length(ia)
		idx_data = [idx_data (idx_firstEntry(i) + 1):ia(i)];
	end
	idx_data = idx_data';
	% data = first time and value added to the other entries data
	data = [raw(idx_data, 1) fillers(idx_data, :) raw(idx_data, 2:4) raw(idx_data, 6:14)];
	features = [2:4 7:13]; results = [14:15];
	if calcFactor
		mdata = mean(data);	stddata = std(data);
		mus = mdata(features); sigmas = stddata(features);
		cin = mus; 	stdin = sigmas;
	else
		mus = cin; sigmas = stdin;
	end
	% keep sex and status 
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
	m1 = bsxfun(@minus, m1, cin);	m1 = bsxfun(@rdivide, m1, stdin);
	m2 = bsxfun(@minus, m2, cin);	m2 = bsxfun(@rdivide, m2, stdin);
	f1 = bsxfun(@minus, f1, cin);	f1 = bsxfun(@rdivide, f1, stdin);
	f2 = bsxfun(@minus, f2, cin);	f2 = bsxfun(@rdivide, f2, stdin);
end

