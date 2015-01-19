function splitdatac(markers, markercsv,testcsv)

raw = load('data.csv');
[ids, ia, ic] = unique( raw(:, 1) );
% check data on how many entried each ID has
% bincount = histc( raw(:,1), ids );
%minsize = min( bincount)
%maxsize = max( bincount )
%minsize_ids = ids( find( bincount == minsize ) );
%maxsize_ids = ids( find( bincount == maxsize ) );

% Split data train-validate-test
iatrain = ia(1:ceil( length(ia) * markers));
iatest = ia( length(iatrain) + 1: end);

ictrainend = find( ic == length(iatrain) + 1, 1) - 1;
ictrain = ic(1:ictrainend);
ictest = ic(ictrainend + 1 : end) - length(iatrain);
%testIdx = ictrainend + 1;

csvwrite(markercsv, raw( 1:ictrainend, :) );
csvwrite(testcsv, raw( (ictrainend + 1):size(raw,1), :) );

end


