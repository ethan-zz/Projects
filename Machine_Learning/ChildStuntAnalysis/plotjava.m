load('Jds.csv');
load('Jws.csv');
load('JVds.csv');
load('JVws.csv');

figure
subplot(2,2,1);
plot(Jws)
title('Weight L Curve');
subplot(2,2,2);
plot(Jds)
title('Date L Curve');
subplot(2,2,3);
plot(JVws)
title('Weight Validate');
subplot(2,2,4);
plot(JVds)
title('Date Validate');

