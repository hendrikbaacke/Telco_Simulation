%% Telco Output Analysis - Group 8

% This code is runnable with MATLAB *only*, since readtable is not a function
% which is implemented in Octave.
% The code is written in a way that makes it nice to read in the MATLAB
% publish mode.
%% File Reading

%make sure that k matches the number of runs n in the Java code of the
%Simulation, k cannot be larger than n 
k = 15;                                                               %<----------------
C_data = cell(k,1);
SortedC_data = cell(k,1);
WaitCons_data = cell(k,1);
WaitCorp_data = cell(k,1);
for i = 1:k
i = i-1;
%retrieve the call data
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
%The .csv files are the output of the Simulation written in Java code, project name 'Telco_Simulation'
fileN = ['Strategy1informationCalls',num2str(i),'.csv'];
C_data{i+1} = readtable(fileN);
C_data{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'};

% sort C_data according to tme_incoming
SortedC_data{i+1} = sortrows(C_data{i+1},3);

%retrieve the waiting times of consumer costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCons = ['Strategy1waitingTimesConsumer',num2str(i),'.csv'];
WaitCons_data{i+1} = readtable(fileWaitCons);
WaitCons_data{i+1}.Properties.VariableNames = {'consumer_wait_tme'};

%retrieve the waiting times of corporate costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCorp = ['Strategy1waitingTimesCorporate',num2str(i),'.csv'];
WaitCorp_data{i+1} = readtable(fileWaitCorp);
WaitCorp_data{i+1}.Properties.VariableNames = {'corporate_wait_tme'};
end


%% Data Retrieval System Configuration 1

%specify amount of runs we want to use for distribution validation
repVal = 15; %repVal <=k                                              %<----------------
%%
if k~=repVal
    disp('_____________________________________');
    disp('Warning: repVal is different from k');
end
consData = cell(repVal,1);
corpData = cell(repVal,1);
serviceTmeCons = cell(repVal,1);
serviceTmeCorp = cell(repVal,1);
interTmeCons = cell(repVal,1);
interTmeCorp = cell(repVal,1);
for i = 1:repVal
disp('__________________________________________________________');
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
%see next section                                                    
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
disp('__________________________________________________________');
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
end


%% Validation of Simulation - Visualization of the Service Time Distribution
%print the data
%consData{1}
%corpData{1}
%______________
%the truncated normal distributions of the service times as specified

pd_serviceTmeCons = makedist('Normal','mu',72 ,'sigma',35);
T_pd_serviceTmeCons = truncate(pd_serviceTmeCons,25,inf);

pd_serviceTmeCorp = makedist('Normal','mu',216 ,'sigma',72);
T_pd_serviceTmeCorp = truncate(pd_serviceTmeCorp,45,inf);

%___________________________________
%Fig.1 Density Histogram plot, superimposing truncated normal dist. for
%consumer service times
x_st1 = 0:.1:230;
figure(1)
%only the data from the first run is used, if set repVal to 10 for example
%we can plot the dist. histogram for any of the repVal we wish by simply
%setting serviceTmeCons{x},serviceTmeCorp to the repVal we want
hist(serviceTmeCons{1},20)
hold on;
plot(x_st1,410000*pdf(T_pd_serviceTmeCons,x_st1),'Color','red','LineWidth',2)
hold off;
xlabel('service time in sec'),ylabel('amount of occurrence');
title('Consumer data: Fitting truncated normal to observed service times');

%___________________________________
%Fig.2 Density Histogram plot, superimposing truncated normal dist. for
%corporate service times
x_st2 = 0:.1:450;
figure(2)
%only the data from the first run is used
hist(serviceTmeCorp{1},20)
hold on
plot(x_st2,280000*pdf(T_pd_serviceTmeCorp,x_st2),'Color','red','LineWidth',2)
hold off
xlabel('service time in sec'),ylabel('amount of occurrence');
title('Corporate data: Fitting normal to observed service times');

%note: the superimposed truncated normal distribution in both figures will
%not be to scale in case the length of the simulation run is changed. The
%provided scalars are heuristically chosen and match 15 days of simulation runtime
%to show that the data indeed stems from the needed distributions. In case
%the simulation is run for longer or shorter, the imposed distribution in both
%distribution histogram plots will of course not be to scale anymore.
%% % Validation of Simulation - Visualization of the Interarrival Times Distribution

%plot a distribution histogram, superimposing exponential dist. for
%consumer and corporate interarrival times
%___________________________________
%Fig.3 Density Histogram plot, superimposing exponential dist. for
%consumer interarrival times
figure(3)
%only the data from the first run is used, if set repVal to 10 for example
%we can plot the dist. histogram for any of the repVal we wish by simply
%setting serviceTmeCons{x},serviceTmeCorp to the repVal we want
%only the data from the first run is used
histfit(interTmeCons{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Consumer data: Fitting exponential to observed interarrvial tmes(s)');

%___________________________________
%Fig.4 Density Histogram plot, superimposing exponential dist. for
%corporate interarrival times
figure(4)
%only the data from the first run is used
histfit(interTmeCorp{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Corporate data: Fitting exponential to observed interarrvial tmes(s)');


%% Replication-Deletion: Visual Procedure
% For the replication deletion the heuristically evaluated 'best' roster (int[][] roster = {{0,5},{2,5},{0,5}};)
% was used which adheres to the given performance requirements while
% keeping the cost of the simulation low at EUR 7760 per day.
% *For reproducing similar results and plots use the roster int[][] roster = {{0, 0, 5}, {2, 0, 5}, {0, 0, 5}}; and
% let the simulation run for 30 days at least 5 times.*
% The system variable waiting time is the most indicative variable with
% regard to the system being in a transient or steady-state, therefore it
% is chosen to plot the waiting times of individual customers over the time in the simulation.
%%
%get days
dayDivisions = cell(k,1);
%get maximum time from each simulation
tmax = cell(k,1);
newDay = 86400; %24*60*60
for i=1:k
d=1;
for j =1:max(SortedC_data{i}{:,5})

    if mod(j,86400)==0
    dayDivisions{i}(d)=j;
    d = d+1;
    end
end
tmax{i}=dayDivisions{i}(length(dayDivisions{i}));
end
%the simulation is started at 6 a.m., the unit is sec. thus 21600sec
tmin = 21600;
%%
%specify how many runs should be used for the replication-deletion
%procedure
if(k>=5)
    replications = 5; %<----------------
else
    replications = 1;
end
if k~=replications
    disp('_____________________________________');
    disp('Warning: replications is different from k');
end

if (repVal<replications)
disp('Error: Ensure that repVal is larger than replications before proceeding');
end

wtTme = cell(replications,1);

wtTmeCons = cell(replications,1);
wtTmeCorp = cell (replications,1);

tmeIncom = cell(replications,1);
tmeIncomCons = cell(replications,1);
tmeIncomCorp = cell(replications,1);
%________________________________________________
% It is decided to retrieve the waiting times in Matlab again, even though
% this data is provided from the Java code, because the visual
% replication-deletion procedure needs to associate the time of arrival of customer
% with the waiting time.

for i = 1:replications
%get the waiting times for both costumer types
wtTme{i} = SortedC_data{i}{:,4}-SortedC_data{i}{:,3};  %<----
%also get the waiting times for both types individually
wtTmeCons{i} = consData{i}{:,4}-consData{i}{:,3};
wtTmeCorp{i} = corpData{i}{:,4}-corpData{i}{:,3};
%get the incoming times of all customers
tmeIncom{i} = SortedC_data{i}{:,3};                    %<----
%also get the incoming times for both types individually
tmeIncomCons{i} =consData{i}{:,3};
tmeIncomCorp{i} =corpData{i}{:,3};
end
%%
% Fig.5 plots the waiting time of each individual
% customer, nonregarding their type for 5 replications of the system.
% The time (x) is in each case the arrival time of the specific customer to the system.
% have at least 5 replications to make Fig.5 meaningful
%___________________________________
%Fig.5 plotting the waiting times of all customers with time on the x-axis
%and output on the y-axis
%xvalues: tme_incoming | yvalues: waiting tme of customer
C = ['.-b','.-y','.-r','.-g','.-k']; % Cell array of colors. length 5, see below reason
ts = cell(replications,1);
figure(5)
for i=1:replications  %plot u INDEPENDENT runs of the simulation
ts{i} = timeseries(wtTme{i},tmeIncom{i});
plot(ts{i},C(i));
hold on
ts{i}.Name = 'Waiting Time of all customer types in sec.';
ts{i}.TimeInfo.Units = 'Seconds';
d11 = ['Time Series Plot: Comparing ',num2str(replications),' runs',': Waiting times of all customers'];
title(d11)
ylabel('Individual Customer Waiting Times in sec.');
end
%ts1.TimeInfo.Format = 'ss.SSS';
xlabel('Simulation Time in sec.')
xlim([tmin tmax{1}])
for i=1:length(dayDivisions{1})
xline(dayDivisions{1}(i),'-.b',{'End of Day ',num2str(i)});
end
%grid on


%%
% %Fig.6,7 and 8,9 plot the waiting time of each individual customer of the two
% specific groups (consumer,corporate). The time (x) is in each case the
% arrival time of the specific customer to the system.

%___________________________________
%Fig.6 plotting the waiting times of each consumer type customers with time on the x-axis
%and output on the y-axis
%xvalues: tme_incoming | yvalues: waiting tme of consumer
figure(6)
tsCons = timeseries(wtTmeCons{1},tmeIncomCons{1});
tsCons.Name = 'Consumer Waiting times, 1. run';
tsCons.TimeInfo.Units = 'Seconds';
plot(tsCons,'.-k')
xlabel('Simulation Time in sec.')
xlim([tmin tmax{1}])
for i=1:length(dayDivisions{1})
xline(dayDivisions{1}(i),'-.b',{'End of Day ',num2str(i)});
end
%grid on

if(replications>=2)
%___________________________________
%Fig.7 plotting the waiting times of each consumer type customers with time on the x-axis
%and output on the y-axis
%xvalues: tme_incoming | yvalues: waiting tme of consumer
figure(7)
tsCons2 = timeseries(wtTmeCons{2},tmeIncomCons{2});
tsCons2.Name = 'Consumer Waiting times, 2. run';
tsCons2.TimeInfo.Units = 'Seconds';
plot(tsCons2,'.-k')
xlabel('Simulation Time in sec.')
xlim([tmin tmax{1}])
for i=1:length(dayDivisions{1})
xline(dayDivisions{1}(i),'-.b',{'End of Day ',num2str(i)});
end
%grid on
end
%%
% ___The explanation is based on (int[][] roster = {{0,5},{2,5},{0,5}};)___
% So the cyclic pattern of the steady-state derives from the waiting times
% of consumer costumers. The pattern stems from the non-stationarity of
% their arrivals. At the middle part of each day (14h-16h), the arrival rate
% is highest, as generated by the thinning algorithm, and we can see this
% clearly reflected in the waiting times of the indiviudal consumers.
% Notice that Fig.6 and Fig.7 exhibit a very similar pattern which
% indicates a specific location for the cutoff-point of the transient.
%%
%___________________________________
%Fig.8 plotting the waiting times of each corporate type customer with time on the x-axis
%and output on the y-axis
%xvalues: tme_incoming | yvalues: waiting tme of corporate
figure(8)
tsCorp = timeseries(wtTmeCorp{1},tmeIncomCorp{1});
tsCorp.Name = 'Corporate Waiting times, 1. run';
tsCorp.TimeInfo.Units = 'Seconds';
plot(tsCorp,'.-k')
xlabel('Simulation Time in sec.')
xlim([tmin tmax{1}])
for i=1:length(dayDivisions{1})
xline(dayDivisions{1}(i),'-.b',{'End of Day ',num2str(i)})  ;
end
%grid on

if(replications>=2)
%___________________________________
%Fig.9 plotting the waiting times of each corporate type customer with time on the x-axis
%and output on the y-axis
%xvalues: tme_incoming | yvalues: waiting tme of corporate

figure(9)
tsCorp2 = timeseries(wtTmeCorp{2},tmeIncomCorp{2});
tsCorp2.Name = 'Corporate Waiting times, 2. run';
tsCorp2.TimeInfo.Units = 'Seconds';
plot(tsCorp2,'.-k')
xlabel('Simulation Time in sec.')
xlim([tmin tmax{1}])
for i=1:length(dayDivisions{1})
xline(dayDivisions{1}(i),'-.b',{'End of Day ',num2str(i)})  ;
end
%grid on
end
%%
% ___The explanation is based on (int[][] roster = {{0,5},{2,5},{0,5}};)___
% After the initial transient upon start of the simulation, the corporate
% customers do not experience any waiting time, suggesting that the
% currently chosen roster caters for the transient only and can be changed
% for something less expensive.


%% Conclusion Replication-Deletion
% The result of the replication/deletion procedure is that the cut-off
% point l is defined to be the end of the first day at 86400 seconds into
% the simulation after the simulation has run for 86400-21600==64800
% seconds. To identify the transient it is sufficient in this case to plot
% the waiting times of customers for 5 runs and visually/heuristically estimate
% the point l. The reason for this being that the cutoff point is very
% pronounced, eliminating the need for averaging over the 5 runs and
% additionally performing a low-pass filter procedure.
% As discussed in the report, after identifying the truncation point l, we
% might want to change the roster based on the aggregate values generated
% from only the desired steady-state.

%% Retrieve the steady-state data of System Configuration 1
% Truncating the first 2 days of data until we can assume that the
% simulation reached its steady-state.
% Therefore do not choose a simulation runtime <15 days to have at least 10
% days of usable data.
% This decision is based on the findings in the previous Section
%'Replication-Deletion: Visual Procedure'
l = 86400*2;                                   %<-------------------------------------   
TruncData = cell(k,1);
%we go to k to ensure that we truncate the transient for all runs of
%simulation, it is a deliberate choice to not add a variable that can have
%a different value from k
for i = 1:k
cutL = (SortedC_data{i}{:,3}>=l);
TruncData{i} = SortedC_data{i}(cutL,:);
end

%validate:
%TruncData{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncData is sorted

%get the individual data for each costumer type
TruncDataCons = cell(k,1);
TruncDataCorp = cell(k,1);

for i = 1:k
%get all data from consumer calls from each sim. run
Co = (TruncData{i}.cstm_tp ==0);
TruncDataCons{i} = TruncData{i}(Co,:);

%get all data from corporate calls from each sim. run
Corp = (TruncData{i}.cstm_tp ==1);
TruncDataCorp{i} = TruncData{i}(Corp,:);
end

%validate:
%TruncDataCons{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncDataCorp{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncData is sorted

%get the waiting times and tms of arrival for all types
TruncwtTme = cell(k,1);
TruncwtTmeCons = cell(k,1);
TruncwtTmeCorp = cell(k,1);


TruncTmeIncom = cell(k,1);
TruncTmeIncomCons = cell(k,1);
TruncTmeIncomCorp = cell(k,1);
for i = 1:k
%get the waiting times for both costumer types from truncated data
TruncwtTme{i} = TruncData{i}{:,4}-TruncData{i}{:,3};

%also get the waiting times for both types individually from truncated data
TruncwtTmeCons{i} = TruncDataCons{i}{:,4}-TruncDataCons{i}{:,3};
TruncwtTmeCorp{i} = TruncDataCorp{i}{:,4}-TruncDataCorp{i}{:,3};

%get the incoming times of all customers
TruncTmeIncom{i} = TruncData{i}{:,3};

%also get the incoming times for both types individually from truncated data
TruncTmeIncomCons{i} =TruncDataCons{i}{:,3};
TruncTmeIncomCorp{i} =TruncDataCorp{i}{:,3};
end

%% Analysis of the Performance Bounds: 1. System Configuration

%Analysing Waiting times of consumer and corp customers with regard to the
%given performance requirements.
%The calling costumers should be assisted in a specified time frame.
%This is reflected in the following requirements:
%
% * 90% of consumers within 5 min
% * 95% of consumers within 10 min
% * 95% of corporate users within 3 min
% * 99% of corporate users within 7 min


ArrayWaitCons_data =  cell(k,1);
ArrayWaitCorp_data =  cell(k,1);

wtCons_smallerFive =  cell(k,1);
wtCons_smallerTen  =  cell(k,1);
wtCorp_smallerThree = cell(k,1);
wtCorp_smallerSeven = cell(k,1);


%we take each of the k runs into account
for i = 1:k

%retrieve the mean waiting times:
Mean_waitingTmeComb(i) = mean(TruncwtTme{i});
Mean_waitingTmeCons(i) = mean(TruncwtTmeCons{i});
Mean_waitingTmeCorp(i) = mean(TruncwtTmeCorp{i});


% data is in sec, convert 5 min to sec
smallerthanFive_Cons = (TruncwtTmeCons{i}<=300);
wtCons_smallerFive{i} = TruncwtTmeCons{i}(smallerthanFive_Cons);

%get the percentage of costumers that have been assisted within 5min:
pct_cons_asstdFive(i) = length(wtCons_smallerFive{i})/length(TruncwtTmeCons{i});


% data is in sec, convert 10 min to sec
smallerthanTen_Cons = (TruncwtTmeCons{i}<=600);
wtCons_smallerTen{i} = TruncwtTmeCons{i}(smallerthanTen_Cons);

%get the percentage of costumers that have been assisted within 10min:
pct_cons_asstdTen(i) = length(wtCons_smallerTen{i})/length(TruncwtTmeCons{i});

%______________________________________________________________________


% data is in sec, convert 3 min to sec
smallerthanThree_Corp = (TruncwtTmeCorp{i}<=180);
wtCorp_smallerThree{i} = TruncwtTmeCorp{i}(smallerthanThree_Corp);

%get the percentage of costumers that have been assisted within 3min:
pct_corp_asstdThree(i) = length(wtCorp_smallerThree{i})/length(TruncwtTmeCorp{i});

% data is in sec, convert 7 min to sec
smallerthanSeven_Corp = (TruncwtTmeCorp{i}<=420);
wtCorp_smallerSeven{i} = TruncwtTmeCorp{i}(smallerthanSeven_Corp);

%get the percentage of costumers that have been assisted within 7min:
pct_corp_asstdSeven(i) = length(wtCorp_smallerSeven{i})/length(TruncwtTmeCorp{i});
end
disp('_____________________________________________________________________________________________________________________________________');
disp('___________________________________System 1:Fractions of each run for each type of performance measure:______________________________');

disp('_____________________________________________________________________________________________________________________________________');
disp('Consumers assisted within 5min:');
pct_cons_asstdFive
disp('_____________________________________________________________________________________________________________________________________');
disp('Consumers assisted within 10min:');
pct_cons_asstdTen
disp('_____________________________________________________________________________________________________________________________________');
disp('Corporate assisted within 3min:');
pct_corp_asstdThree
disp('_____________________________________________________________________________________________________________________________________');
disp('Corporate assisted within 7min:');
pct_corp_asstdSeven


disp('_____________________________________________________________________________________________________________________________________');
disp('________________________________________________________System 1:Performance Measures:_______________________________________________');

disp('_____________________________________________________________________________________________________________________________________');
dcomb = ['The expected waiting time based on all ',num2str(k),' runs is in seconds: '];
disp(dcomb);
disp(mean(Mean_waitingTmeComb));
disp('_____________________________________________________________________________________________________________________________________');
d5 = ['The expected waiting time for consumers based on all ',num2str(k),' runs is in seconds: '];
disp(d5);
disp(mean(Mean_waitingTmeCons));
disp('_____________________________________________________________________________________________________________________________________');
d6 = ['The expected waiting time for corporates based on all ',num2str(k),' runs is in seconds: '];
disp(d6);
disp(mean(Mean_waitingTmeCorp));
disp('_____________________________________________________________________________________________________________________________________');
d7 = ['The fraction of consumers that have been assisted within 5 minutes, averaged over ',num2str(k),' runs is: '];
disp(d7);
disp(mean(pct_cons_asstdFive));
disp('_____________________________________________________________________________________________________________________________________');
d8 = ['The fraction of consumers that have been assisted within 10 minutes, averaged over ',num2str(k),' runs is: '];
disp(d8);
disp(mean(pct_cons_asstdTen));
disp('_____________________________________________________________________________________________________________________________________');
d9 = ['The fraction of corporates that have been assisted within 3 minutes, averaged over ',num2str(k),' runs is: '];
disp(d9);
disp(mean(pct_corp_asstdThree));
disp('_____________________________________________________________________________________________________________________________________');
d10 = ['The fraction of corporates that have been assisted within 7 minutes, averaged over ',num2str(k),' runs is: '];
disp(d10);
disp(mean(pct_corp_asstdSeven));

%% Confidence intervals: System Configuration 1
% t-confidence intervals of the mean
% It is a small sample size: 20
% The runs are independent
% As this concerns means, the data is normally distributed

% We want to have a probability of 95% of all the means falling in the
% confidence intervals simultaneously.

% The confidence intervals that are created:
% - Average waiting time
% - Performance measure 1: percentage of consumers assisted within 5
% minutes
%- Performance measure 2: percentage of consumers assisted within 10
%minutes
% Performance measure 3: percentage of corporate customers assisted within
% 3 minutes
% Performance meaure 4: percentage of corporate customers assisted within 7
% minutes

% This are 5 confidence intervals. As the combined probability needs to
% 95%, due to the Bonferroni inequality, the alpha for each of these will
% be 0.01

                                                          
% We have 5 CIs, to have overall confidence level of 95% have individual
% alphas = 0.05
alpha1=0.01;
gamma1=(1-alpha1/2);
%get the degrees of freedom
dof1 = k-1;
% get the critical point of t-dist.
t_crit = tinv(gamma1,dof1);  

%%
disp('_____________________________________________________________________________________________________________________________________');
disp('_______________________________________________System 1:Confidence Intervals:________________________________________________________');
% For the average waiting time:
mean_average_waiting = mean(Mean_waitingTmeComb);
var_avg_wait = var(Mean_waitingTmeComb);
ci_average_waiting_time = [(mean_average_waiting - t_crit * sqrt(var_avg_wait/k)),( mean_average_waiting + t_crit * sqrt(var_avg_wait/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the mean waiting time (in seconds) is: ');
disp(ci_average_waiting_time);

% For the first performance measure:
mean_perf_m_1 = mean(pct_cons_asstdFive);
var_perf_m_1 = var(pct_cons_asstdFive);
ci_perf_m_1 = [(mean_perf_m_1 - t_crit * sqrt(var_perf_m_1/k)),( mean_perf_m_1 + t_crit * sqrt(var_perf_m_1/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of consumers assisted within 5 minutes is: ');
disp(ci_perf_m_1);

% For the second performance measure:
mean_perf_m_2 = mean(pct_cons_asstdTen);
var_perf_m_2 = var(pct_cons_asstdTen);
ci_perf_m_2 = [(mean_perf_m_2 - t_crit * sqrt(var_perf_m_2/k)),( mean_perf_m_2 + t_crit * sqrt(var_perf_m_2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of consumers assisted within 10 minutes is: ');
disp(ci_perf_m_2);

% For the third performance measure:
mean_perf_m_3 = mean(pct_corp_asstdThree);
var_perf_m_3 = var(pct_corp_asstdThree);
ci_perf_m_3 = [(mean_perf_m_3 - t_crit * sqrt(var_perf_m_3/k)),( mean_perf_m_3 + t_crit * sqrt(var_perf_m_3/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of corporate customers assisted within 3 minutes is: ');
disp(ci_perf_m_3);

% For the fourth performance measure:
mean_perf_m_4 = mean(pct_corp_asstdSeven);
var_perf_m_4 = var(pct_corp_asstdSeven);
ci_perf_m_4 = [(mean_perf_m_4 - t_crit * sqrt(var_perf_m_4/k)),( mean_perf_m_4 + t_crit * sqrt(var_perf_m_4/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of corporate customers assisted within 7 minutes is: ');
disp(ci_perf_m_4);


%% Comparing Two different systems 
% NOTE: all data analysis until now is performed with System configuration 1 .
%%
% 
% # System 1 'Flexible' : CSA corporate helps always when the queue of corporate
%   costumers is empty. Thus, Strategy1 uses only flexible corporate CSAs.
% # System 2 'Mixed' : A specified amount of CSA corporates is held idle in order to
%   help incoming corporate callers. 
% 

%% Data Retrieval System Configuration 2

%the amount of runs for both systems should be the same to keep things
%simple, thus we use k

C_data2 = cell(k,1);
SortedC_data2 = cell(k,1);
WaitCons_data2 = cell(k,1);
WaitCorp_data2 = cell(k,1);
for i = 1:k
i = i-1;
%retrieve the call data
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
%The .csv files are the output of the Simulation written in Java code, project name 'Telco_Simulation'
fileN2 = ['Strategy2informationCalls',num2str(i),'.csv'];
C_data2{i+1} = readtable(fileN2);
C_data2{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'};

% sort C_data2 according to tme_incoming
SortedC_data2{i+1} = sortrows(C_data2{i+1},3);

%retrieve the waiting times of consumer costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCons2 = ['Strategy2waitingTimesConsumer',num2str(i),'.csv'];
WaitCons_data2{i+1} = readtable(fileWaitCons2);
WaitCons_data2{i+1}.Properties.VariableNames = {'consumer_wait_tme'};

%retrieve the waiting times of corporate costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCorp2 = ['Strategy2waitingTimesCorporate',num2str(i),'.csv'];
WaitCorp_data2{i+1} = readtable(fileWaitCorp2);
WaitCorp_data2{i+1}.Properties.VariableNames = {'corporate_wait_tme'};
end


%%  Retrieve the steady-state data of System Configuration 2
% For getting the steady-state data we make the simplifying but reasonable assumption that
% the (generous) truncation of the first 2 days, which was found to eliminate
% the transient for the first system configuration, also cuts off the
% transient of system 2.

TruncData2 = cell(k,1);
%we go to k to ensure that we truncate the transient for all runs of
%simulation, it is a deliberate choice to not add a variable that can have
%a different value from k
for i = 1:k
cutL2 = (SortedC_data2{i}{:,3}>=l);
TruncData2{i} = SortedC_data2{i}(cutL2,:);
end

%validate:
%TruncData2{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncData is sorted

%get the individual data for each costumer type
TruncDataCons2 = cell(k,1);
TruncDataCorp2 = cell(k,1);

for i = 1:k
%get all data from consumer calls from each sim. run
Co2 = (TruncData2{i}.cstm_tp ==0);
TruncDataCons2{i} = TruncData2{i}(Co2,:);

%get all data from corporate calls from each sim. run
Corp2 = (TruncData2{i}.cstm_tp ==1);
TruncDataCorp2{i} = TruncData2{i}(Corp2,:);
end

%validate:
%TruncDataCons2{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncDataCorp2{1}(1:15,{'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'})
%TruncData2 is sorted

%get the waiting times and tms of arrival for all types
TruncwtTme2 = cell(k,1);
TruncwtTmeCons2 = cell(k,1);
TruncwtTmeCorp2 = cell(k,1);


TruncTmeIncom2 = cell(k,1);
TruncTmeIncomCons2 = cell(k,1);
TruncTmeIncomCorp2 = cell(k,1);
for i = 1:k
%get the waiting times for both costumer types from truncated data
TruncwtTme2{i} = TruncData2{i}{:,4}-TruncData2{i}{:,3};

%also get the waiting times for both types individually from truncated data
TruncwtTmeCons2{i} = TruncDataCons2{i}{:,4}-TruncDataCons2{i}{:,3};
TruncwtTmeCorp2{i} = TruncDataCorp2{i}{:,4}-TruncDataCorp2{i}{:,3};

%get the incoming times of all customers
TruncTmeIncom2{i} = TruncData2{i}{:,3};

%also get the incoming times for both types individually from truncated data
TruncTmeIncomCons2{i} =TruncDataCons2{i}{:,3};
TruncTmeIncomCorp2{i} =TruncDataCorp2{i}{:,3};
end

%% Analysis of the Performance Bounds: 2. System Configuration


ArrayWaitCons_data2 =  cell(k,1);
ArrayWaitCorp_data2 =  cell(k,1);

wtCons_smallerFive2 =  cell(k,1);
wtCons_smallerTen2  =  cell(k,1);
wtCorp_smallerThree2 = cell(k,1);
wtCorp_smallerSeven2 = cell(k,1);


%we take each of the k runs into account
for i = 1:k

%retrieve the mean waiting times:
Mean_waitingTmeComb2(i) = mean(TruncwtTme2{i});
Mean_waitingTmeCons2(i) = mean(TruncwtTmeCons2{i});
Mean_waitingTmeCorp2(i) = mean(TruncwtTmeCorp2{i});


% data is in sec, convert 5 min to sec
smallerthanFive_Cons2 = (TruncwtTmeCons2{i}<=300);
wtCons_smallerFive2{i} = TruncwtTmeCons2{i}(smallerthanFive_Cons2);

%get the percentage of costumers that have been assisted within 5min:
pct_cons_asstdFive2(i) = length(wtCons_smallerFive2{i})/length(TruncwtTmeCons2{i});


% data is in sec, convert 10 min to sec
smallerthanTen_Cons2 = (TruncwtTmeCons2{i}<=600);
wtCons_smallerTen2{i} = TruncwtTmeCons2{i}(smallerthanTen_Cons2);

%get the percentage of costumers that have been assisted within 10min:
pct_cons_asstdTen2(i) = length(wtCons_smallerTen2{i})/length(TruncwtTmeCons2{i});

%______________________________________________________________________


% data is in sec, convert 3 min to sec
smallerthanThree_Corp2 = (TruncwtTmeCorp2{i}<=180);
wtCorp_smallerThree2{i} = TruncwtTmeCorp2{i}(smallerthanThree_Corp2);

%get the percentage of costumers that have been assisted within 3min:
pct_corp_asstdThree2(i) = length(wtCorp_smallerThree2{i})/length(TruncwtTmeCorp2{i});

% data is in sec, convert 7 min to sec
smallerthanSeven_Corp2 = (TruncwtTmeCorp2{i}<=420);
wtCorp_smallerSeven2{i} = TruncwtTmeCorp2{i}(smallerthanSeven_Corp2);

%get the percentage of costumers that have been assisted within 7min:
pct_corp_asstdSeven2(i) = length(wtCorp_smallerSeven2{i})/length(TruncwtTmeCorp2{i});
end

disp('_____________________________________________________________________________________________________________________________________');
disp('___________________________________System 2:Fractions of each run for each type of performance measure:______________________________');

disp('_____________________________________________________________________________________________________________________________________');
disp('Consumers assisted within 5min:');
pct_cons_asstdFive2
disp('_____________________________________________________________________________________________________________________________________');
disp('Consumers assisted within 10min:');
pct_cons_asstdTen2
disp('_____________________________________________________________________________________________________________________________________');
disp('Corporate assisted within 3min:');
pct_corp_asstdThree2
disp('_____________________________________________________________________________________________________________________________________');
disp('Corporate assisted within 7min:');
pct_corp_asstdSeven2


disp('_____________________________________________________________________________________________________________________________________');
disp('________________________________________________________System 2:Performance Measures:_______________________________________________');

disp('_____________________________________________________________________________________________________________________________________');
dcomb = ['The expected waiting time based on all ',num2str(k),' runs is in seconds: '];
disp(dcomb);
disp(mean(Mean_waitingTmeComb2));
disp('_____________________________________________________________________________________________________________________________________');
d5 = ['The expected waiting time for consumers based on all ',num2str(k),' runs is in seconds: '];
disp(d5);
disp(mean(Mean_waitingTmeCons2));
disp('_____________________________________________________________________________________________________________________________________');
d6 = ['The expected waiting time for corporates based on all ',num2str(k),' runs is in seconds: '];
disp(d6);
disp(mean(Mean_waitingTmeCorp2));
disp('_____________________________________________________________________________________________________________________________________');
d7 = ['The fraction of consumers that have been assisted within 5 minutes, averaged over ',num2str(k),' runs is: '];
disp(d7);
disp(mean(pct_cons_asstdFive2));
disp('_____________________________________________________________________________________________________________________________________');
d8 = ['The fraction of consumers that have been assisted within 10 minutes, averaged over ',num2str(k),' runs is: '];
disp(d8);
disp(mean(pct_cons_asstdTen2));
disp('_____________________________________________________________________________________________________________________________________');
d9 = ['The fraction of corporates that have been assisted within 3 minutes, averaged over ',num2str(k),' runs is: '];
disp(d9);
disp(mean(pct_corp_asstdThree2));
disp('_____________________________________________________________________________________________________________________________________');
d10 = ['The fraction of corporates that have been assisted within 7 minutes, averaged over ',num2str(k),' runs is: '];
disp(d10);
disp(mean(pct_corp_asstdSeven2));

%% Confidence intervals: System Configuration 2


% This are 5 confidence intervals. As the combined probability needs to
% 95%, due to the Bonferroni inequality, the alpha for each of these will
% be 0.01

                                                          
% We have 5 CIs, to have overall confidence level of 95% have individual
% alphas = 0.05

% the computation of the critical t-val is the same as for the CIs of
% System config. 1 as we are using the same k and also want the overall
% confidence level to be 95 percent, so take alpha1=0.01 and k-1

%%
disp('_____________________________________________________________________________________________________________________________________');
disp('_______________________________________________System 2:Confidence Intervals:________________________________________________________');
% For the average waiting time:
mean_average_waiting2 = mean(Mean_waitingTmeComb2);
var_avg_wait2 = var(Mean_waitingTmeComb2);
ci_average_waiting_time2 = [(mean_average_waiting2 - t_crit * sqrt(var_avg_wait2/k)),( mean_average_waiting2 + t_crit * sqrt(var_avg_wait2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the mean waiting time (in seconds) is: ');
disp(ci_average_waiting_time2);

% For the first performance measure:
mean_perf_m_1_2 = mean(pct_cons_asstdFive2);
var_perf_m_1_2 = var(pct_cons_asstdFive2);
ci_perf_m_1_2 = [(mean_perf_m_1_2 - t_crit * sqrt(var_perf_m_1_2/k)),( mean_perf_m_1_2 + t_crit * sqrt(var_perf_m_1_2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of consumers assisted within 5 minutes is: ');
disp(ci_perf_m_1_2);

% For the second performance measure:
mean_perf_m_2_2 = mean(pct_cons_asstdTen2);
var_perf_m_2_2 = var(pct_cons_asstdTen2);
ci_perf_m_2_2 = [(mean_perf_m_2_2 - t_crit * sqrt(var_perf_m_2_2/k)),( mean_perf_m_2_2 + t_crit * sqrt(var_perf_m_2_2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of consumers assisted within 10 minutes is: ');
disp(ci_perf_m_2_2);

% For the third performance measure:
mean_perf_m_3_2 = mean(pct_corp_asstdThree2);
var_perf_m_3_2 = var(pct_corp_asstdThree2);
ci_perf_m_3_2 = [(mean_perf_m_3_2 - t_crit * sqrt(var_perf_m_3_2/k)),( mean_perf_m_3_2 + t_crit * sqrt(var_perf_m_3_2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of corporate customers assisted within 3 minutes is: ');
disp(ci_perf_m_3_2);

% For the fourth performance measure:
mean_perf_m_4_2 = mean(pct_corp_asstdSeven2);
var_perf_m_4_2 = var(pct_corp_asstdSeven2);
ci_perf_m_4_2 = [(mean_perf_m_4_2 - t_crit * sqrt(var_perf_m_4_2/k)),( mean_perf_m_4_2 + t_crit * sqrt(var_perf_m_4_2/k))];
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the percentage of corporate customers assisted within 7 minutes is: ');
disp(ci_perf_m_4_2);



%% Comparison of System Configuration 1 and 2

%% 1. 95 percent paired confidence interval on the difference in expected waiting time
% Do the Paired confidence interval approach as n1==n2
disp('_____________________________________________________________________________________________________________________________________');
disp('________________________________________________Confidence Interval of Difference:___________________________________________________');
%Compute the paired means zeta
zeta = (Mean_waitingTmeComb-Mean_waitingTmeComb2);
zetaBar = sum(zeta)/k;

%We want to get 0.95 confidence interval of difference
alpha2=0.05;
gamma2=(1-alpha2/2);
%get the degrees of freedom
dof2 = k-1;
% get the critical point of t-dist.
t_dff = tinv(gamma2,dof2); 

%Compute the upper and lower bound of the confidence interval, if 0 is not
%part of this interval, we have a significant difference of the two systems
ci_difference(1,1) = zetaBar - t_dff*sqrt(var(zeta)/k);
ci_difference(1,2) = zetaBar + t_dff*sqrt(var(zeta)/k);
disp('_____________________________________________________________________________________________________________________________________');
disp('The confidence interval for the difference of expected waiting time of System 1 and 2 is: ');
disp(ci_difference);
%%
% We want to reduce/minimize waiting time, therefore if only values <0 , then System 1
% has significantly less waiting time than System 2. For the converse case when there are only values >0, then
% System 2 has significantly less waiting time than System 1.


%% 2. Paired T-test 
% Null hypothesis H_0: 
% 
% 
% * The expected waiting times for the customers for system configuration
% 1 (flexible) and  2 (mixed) does not significantly differ from one to
% another. (Mean_waitingTmeComb = Mean_waitingTmeComb2)
disp('_____________________________________________________________________________________________________________________________________');
disp('Value of the paired t-test test statistic: ');
t_statVal = abs(zetaBar/sqrt(var(zeta/k)))
%we have the same dof as for the CI of difference computed above
%Note:allowed to use 'tcdf' and 'tinv' according to requirements 
pctTl = tcdf(t_statVal,dof2); 
disp('_____________________________________________________________________________________________________________________________________');
disp('Obtained p-value of the paired t-test: ');
pVal =2*(1-pctTl)
%%
% If pVal <0.05 then we reject the null hypothesis that the average waiting time of costumers in the two systems
% is not significantly difference, otherwise we cannot reject H_0.




%Version1 29/05/20 00:08 H.Baacke
