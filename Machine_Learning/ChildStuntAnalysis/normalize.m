function [mus, sigmas, normdata] = normalize(data)
	mus = mean(data);
	sigmas = std(data);
	normdata = bsxfun(@minus, data, mus);
	normdata = bsxfun(@rdivide, normdata, sigmas);
end

