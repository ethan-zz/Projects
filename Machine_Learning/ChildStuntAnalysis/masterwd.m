%if ~exist('X')
	close all;
	clear all;
%end

highorder = 0;
doNormalize = 0;
[X, t, H, V, Vt, VH, Test, TestT, TestH] = loaddata(highorder);
% Normalize training data
if doNormalize
	[mus, sigmas, X] = normalize(X);
end
X = [ones(size(X, 1),1) X];
% Normalize validation data
if doNormalize
	V = bsxfun(@minus, V, mus);
	V = bsxfun(@rdivide, V, sigmas);
end
V = [ones(size(V, 1),1) V];
% Normalize test data
if doNormalize
	Test = bsxfun(@minus, Test, mus);
	Test = bsxfun(@rdivide, Test, sigmas);
end
Test = [ones(size(Test, 1),1) Test];

rate = 0.003;
iter = 1000;

%lambdaws = [0 0.3 0.7 1 3 7 10 30 70 100 300 700 1000 3000 7000];
%lambdads = [0 0.3 0.7 1 3 7 10 30 70 100 300 700 1000 3000 7000];
lambdaws = [0 0.1 0.3 0.9 1.2];
lambdads = [0 0.1 0.3 0.9 1.2];

[thetaw, thetad] = estimatewd(X, t, V, Vt, iter, rate, lambdaws, lambdads);
yall = Test * [thetaw thetad];

mintall = min(TestT)
maxtall = max(TestT)
errall = abs( yall - TestT );
minerrall = min(errall)
maxerrall = max(errall)
relerrall = (errall ./ TestT) * 100;
minrelall = min(relerrall)
maxrelall = max(relerrall)

raw = load('test.csv');
testIds = raw(:, 1);
testwd = raw(:, 13:14);
clear raw;
[ids, ia, ic] = unique( testIds );
target = testwd( ia, : );
idx_firstEntry = [1; ia(1:end-1) + 1];
idx_data = [];
for i = 1:length(ia)
	idx_data = [idx_data (idx_firstEntry(i) + 1):ia(i)];
end
%remove th first entry of ID to line up indexes with estimate results
% Sanity check
testwd = testwd( idx_data, :);
consistent = sum( testwd ~= TestT );
clear testwd consistent;

testIds = testIds( idx_data ); clear idx_data;
[ids, ia, ic] = unique( testIds ); clear ic;
idx_firstEntry = [1; ia(1:end-1) + 1];
y = [];
for i = 1:length(ia)
	if idx_firstEntry(i) == ia(i)
		y = [y; yall(ia(i), :)];
	else
		%y = [y; median( yall(idx_firstEntry(i):ia(i), :) )];
		y = [y; mean( yall(idx_firstEntry(i):ia(i), :) )];
	end
end
err = abs( y - target);
minerr = min(err)
maxerr = max(err)
mint = min(target);
maxt = max(target);
relerr = (err ./ target) * 100;
minrel = min(relerr)
maxrel = max(relerr)

score = calcscore( y, target, TestH )
mt = mean(target);
tmp = target - mt(ones(size(y,1), 1), :);
sstot = sum(sum( tmp.^2))
ssres = sum(sum((y-target).^2));
score2 = 1 - ssres/sstot


