function dout = scaledata(din, mus, sigmas)
	dout = bsxfun(@minus, din, mus);
	dout = bsxfun(@rdivide, dout, sigmas);
end

