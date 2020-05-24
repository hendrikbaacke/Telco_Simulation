
% This is code runnable with MATLAB only. readtable is not a function
% implemented in Octave


%% Data Retrieval 

%specify number of runs of simulation
k = 1;
C_data = cell(k,1);
SortedC_data = cell(k,1);
WaitCons_data = cell(k,1);
WaitCorp_data = cell(k,1);
for i = 1:k
i = i-1;
%retrieve the call data
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileN = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/informationCalls',num2str(i),'.csv'];
C_data{i+1} = readtable(fileN);
C_data{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'};

% sort C_data according to tme_incoming
SortedC_data{i+1} = sortrows(C_data{i+1},3);

%retrieve the waiting times of consumer costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCons = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesConsumer',num2str(i),'.csv'];
WaitCons_data{i+1} = readtable(fileWaitCons);
WaitCons_data{i+1}.Properties.VariableNames = {'consumer_wait_tme'};

%retrieve the waiting times of corporate costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCorp = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesCorporate',num2str(i),'.csv'];
WaitCorp_data{i+1} = readtable(fileWaitCorp);
WaitCorp_data{i+1}.Properties.VariableNames = {'corporate_wait_tme'};
end


%% Validation of Simulation

%specify amount of runs we want to use for distribution validation
repVal = 1;

consData = cell(repVal,1);
corpData = cell(repVal,1);
serviceTmeCons = cell(repVal,1);
serviceTmeCorp = cell(repVal,1);
wtTmeCons = cell(repVal,1);
wtTmeCorp = cell (repVal,1);

for i = 1:repVal
%get all data from consumer calls from each sim. run
ConsTbl = (SortedC_data{i}.cstm_tp ==0);
consData{i} = SortedC_data{i}(ConsTbl,:);

%get all data from corporate calls from each sim. run
CorpTbl = (SortedC_data{i}.cstm_tp ==1);
corpData{i} = SortedC_data{i}(CorpTbl,:);
%________________________________________________
%get the service times of consumers
serviceTmeCons{i} = consData{i}{:,5}-consData{i}{:,4};
d1 = ['Shortest consumer call service time in ',num2str(i), '. run: ']; 
d2 = ['Longest consumer call service time in ',num2str(i), '. run: ' ];
disp(d1)
%see next section                                                    !!
disp(min(serviceTmeCons{i}))
disp(d2)
%highest values are not truncated
disp(max(serviceTmeCons{i}))

%get the service times of corporates
serviceTmeCorp{i} = corpData{i}{:,5}-corpData{i}{:,4};
d3 = ['Shortest corporate call service time in ',num2str(i), '. run: ']; 
d4 = ['Longest corporate call service time in ',num2str(i), '. run: ' ];
disp(d3)
%smallest values are >45 as desired
disp(min(serviceTmeCorp{i}))
disp(d4)
%highest values are not truncated
disp(max(serviceTmeCorp{i}))
%________________________________________________
%get the interarrival tmes of consumers
%Simulation starts at 21600

interTmeCons{i}(1) =  (consData{i}{1:1,{'tme_incoming'}}-21600);

for j = 1:(height(consData{i})-1)  
interTmeCons{i}(j+1) = (consData{i}{j+1:j+1,{'tme_incoming'}}-consData{i}{j:j,{'tme_incoming'}});
end

%get the interarrival tmes of corporates
interTmeCorp{i}(1) =  (corpData{i}{1:1,{'tme_incoming'}}-21600);

for j = 1:(height(corpData{i})-1)  
interTmeCorp{i}(j+1) = (corpData{i}{j+1:j+1,{'tme_incoming'}}-corpData{i}{j:j,{'tme_incoming'}});
end

%________________________________________________
%(not necessarily needed as this is done in Java already)
%get the waiting times of consumers
wtTmeCons{i} = consData{i}{:,4}-consData{i}{:,3};
%get the waiting times of corporates
wtTmeCorp{i} = corpData{i}{:,4}-corpData{i}{:,3};
end

%%
%print the data
%consData{1}
%corpData{1}
%______________
%embedd this in loop of previous section if wish to plot more runs
%here only the data from the first run is used
%plot a distribution histogram, superimposing normal dist. for
%consumer and corporate service times

%the truncated normal distributions of the service times as specified

pd_serviceTmeCons = makedist('Normal','mu',72 ,'sigma',35);
T_pd_serviceTmeCons = truncate(pd_serviceTmeCons,25,inf);

pd_serviceTmeCorp = makedist('Normal','mu',216 ,'sigma',72);
T_pd_serviceTmeCorp = truncate(pd_serviceTmeCorp,45,inf);



% xline = linspace(0,100);
% figure(1)
% hist(serviceTmeCons{1},20)
% hold on
% plot(xline,pdf(T_pd_serviceTmeCons,xline))
% hold off
% xlabel('service time in sec'),ylabel('amount of occurrence');
% 
% title('Consumer data: Fitting truncated normal to observed service tmes');
% 
% figure(2)
% hist(serviceTmeCorp{1},20)
% hold on
% plot(xline,pdf(T_pd_serviceTmeCorp,xline))
% hold off
% xlabel('service time in sec'),ylabel('amount of occurrence');
% title('Corporate data: Fitting normal to observed service tmes');

%plot a distribution histogram, superimposing exponential dist. for
%consumer and corporate interarrival times
figure(3)
histfit(interTmeCons{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Consumer data: Fitting exponential to observed interarrvial tmes(s)');
figure(4)
histfit(interTmeCorp{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Corporate data: Fitting exponential to observed interarrvial tmes(s)');


%% Analysis of the Performance Bounds

%Analysing Waiting times of consumer and corp cust.
%consumer:
wtTmeCons = table2array(WaitCons_data{1});
figure(5)
histfit(wtTmeCons,[],'exponential');
figure(6)
hist(wtTmeCons)
Mean_waitingTmeCons = mean(wtTmeCons)

%corporate:
wtTmeCorp = table2array(WaitCorp_data{1});
figure(7)
Mean_waitingTmeCorp = mean(wtTmeCorp)
histfit(wtTmeCorp,[],'exponential');
figure(8)
hist(wtTmeCorp)


%%

ArrayWaitCons_data = cell(k,1);
ArrayWaitCorp_data = cell(k,1);

wtCons_smallerFive = cell(k,1);
wtCons_smallerTen  = cell(k,1);
wtCorp_smallerThree = cell(k,1);
wtCorp_smallerSeven = cell(k,1);

%we take each of the k runs into account
for i = 1:k
ArrayWaitCons_data{i} = table2array(WaitCons_data{i});
ArrayWaitCorp_data{i} = table2array(WaitCorp_data{i});

% data is in sec, convert 5 min to sec
smallerthanFive_Cons = (ArrayWaitCons_data{i}<=300); 
wtCons_smallerFive{i} = ArrayWaitCons_data{i}(smallerthanFive_Cons);

%get the percentage of costumers that have been assisted within 5min:
pct_cons_asstdFive(i) = length(wtCons_smallerFive{i})/height(WaitCons_data{i})


% data is in sec, convert 10 min to sec
smallerthanTen_Cons = (ArrayWaitCons_data{i}<=600);
wtCons_smallerTen{i} = ArrayWaitCons_data{i}(smallerthanTen_Cons);

%get the percentage of costumers that have been assisted within 10min:
pct_cons_asstdTen(i) = length(wtCons_smallerTen{i})/height(WaitCons_data{i})

%______________________________________________________________________


% data is in sec, convert 3 min to sec
smallerthanThree_Corp = (ArrayWaitCorp_data{i}<=180); 
wtCorp_smallerThree{i} = ArrayWaitCorp_data{i}(smallerthanThree_Corp);

%get the percentage of costumers that have been assisted within 3min:
pct_corp_asstdThree(i) = length(wtCorp_smallerThree{i})/height(WaitCorp_data{i})

% data is in sec, convert 7 min to sec
smallerthanSeven_Corp = (ArrayWaitCorp_data{i}<=420); 
wtCorp_smallerSeven{i} = ArrayWaitCorp_data{i}(smallerthanSeven_Corp);

%get the percentage of costumers that have been assisted within 7min:
pct_corp_asstdSeven(i) = length(wtCorp_smallerSeven{i})/height(WaitCorp_data{i})
end

disp('_____________________________________________________________________________________________________________________________________');
d5 = ['The fraction of consumers that have been assisted within five minutes, averaged over ',num2str(k),' runs is: '];
disp(d5);
disp(mean(pct_cons_asstdFive))
disp('_____________________________________________________________________________________________________________________________________');
d6 = ['The fraction of consumers that have been assisted within ten minutes, averaged over ',num2str(k),' runs is: ',mean(pct_cons_asstdTen)];
disp(d6);
disp(mean(pct_cons_asstdTen))
disp('_____________________________________________________________________________________________________________________________________');
d7 = ['The fraction of corporates that have been assisted within three minutes, averaged over ',num2str(k),' runs is: ',mean(pct_corp_asstdThree)];
disp(d7);
disp(mean(pct_corp_asstdThree))
disp('_____________________________________________________________________________________________________________________________________');
d8 = ['The fraction of corporates that have been assisted within seven minutes, averaged over ',num2str(k),' runs is: ',mean(pct_corp_asstdSeven)];
disp(d8);
disp(mean(pct_corp_asstdSeven))


%%
% corpData{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%corpData{1}



%isequal(wtTmeCons{1}(bolWaitCons1),ArrayWaitCons_data(bolWaitCons))
% down to parallel threads? 




 % bolWaitCons1 = (wtTmeCons{1}~=0);
% wtTmeCons{1}(bolWaitCons1);



%length(wtTmeCons{1}(bolWaitCons1))
% length(ArrayWaitCons_data(bolWaitCons))
% 
% %not the same! WHY
% height(WaitCons_data{1});
% height(consData{1});