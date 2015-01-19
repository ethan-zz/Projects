function [data, target, M] = composedata(csv, highorder)
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
	data = [fillers(idx_data, :) raw(idx_data, 2:4) raw(idx_data, 6:12)];
	if highorder
		data = secondorder( data );
	end
	target = raw(idx_data, 13:14);
end

