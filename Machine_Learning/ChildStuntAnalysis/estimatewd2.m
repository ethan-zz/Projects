function [thetawf, thetadf] = estimatewd2(X, t, V, Vt, iter, rate, lambdaws, lambdads)

lambdaw = 0;
lambdad = 0;
thetaw = zeros( size(X,2), 1);
Jws = zeros(1, iter);
Jds = Jws;

[JVw0, ignore] = costwd(V, Vt(:,1), thetaw, 0 );
JVws = zeros(1, length(lambdaws) );
thetawf = thetaw;
JVwt = JVw0;
for j = 1:length(lambdaws)
	thetaw = zeros( size(X,2), 1);
	lambdaw = lambdaws(j);
	for i = 1:iter
		[Jw, gradientw] = costwd(X, t(:,1), thetaw, lambdaw );
		Jws(i) = Jw;
		thetaw = thetaw - rate * gradientw;
	end
	[JVw1, ignore] = costwd(V, Vt(:,1), thetaw, 0 );
	JVws(j) = JVw1;
	if JVw1 < JVwt
		JVwt = JVw1;
		thetawf = thetaw;
		Jwsf = Jws;
		jwf = j
	end
end

Xd = [X X*thetawf]; % Add estimated w as a feature for d
Vd = [V V*thetawf]; % Add estimated w as a feature for d
thetad = zeros( size(Vd,2), 1); % add estimated w as another feature of d
[JVd0, ignore] = costwd(Vd, Vt(:,2), thetad, 0 );
JVds = zeros(1, length(lambdads) );
thetadf = thetad;
JVdt = JVd0;
for j = 1:length(lambdads)
	thetad = zeros( size(Xd,2), 1);
	lambdad = lambdads(j);
	for i = 1:iter
		[Jd, gradientd] = costwd(Xd, t(:,2), thetad, lambdad );
		Jds(i) = Jd;
		thetad = thetad - rate * gradientd;
	end
	[JVd1, ignore] = costwd(Vd, Vt(:,2), thetad, 0 );
	JVds(j) = JVd1;
	if JVd1 < JVdt
		JVdt = JVd1;
		thetadf = thetad;
		Jdsf = Jds;
		jvf =j
	end
end

[Jw, ignore] = costwd(X, t(:,1), thetawf, 0 );
[Jd, ignore] = costwd(Xd, t(:,2), thetadf, 0 );

JVw0, JVwt
JVd0, JVdt
Jw, Jd

figure
subplot(2,2,1);
plot(1:iter, Jwsf);
title('Weight L Curve');
subplot(2,2,2);
plot(1:iter, Jdsf);
title('Date L Curve');

subplot(2,2,3);
plot(1:length(lambdaws), JVws, '*-');
title('Weight Validate');
subplot(2,2,4);
plot(1:length(lambdads), JVds, '*-');
title('Date Validate');

end

