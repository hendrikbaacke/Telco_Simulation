%specify number of runs
k = 1;


Cdata = cell(k,1);
for i = 1:k
i = i-1;
fileN = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/informationCalls',num2str(i),'.csv'];
Cdata{i+1} = readtable(fileN)
Cdata{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_answered' 'tme_endend'};
end


for n = Cdata{1}(1):Cdata{1}(end)
if Cdata
    serviceTmeCust = Cdata{1}{(i),5}-Cdata{1}{(i),4}
end
end
    Cdata{1}(1:100,[1 2 3 4 5])