# Import packages.
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm
import statsmodels
from sklearn.ensemble import RandomForestClassifier

# Set data folder.
folder_path = './Nino_monthly.xlsx'

# Read Oceanic Nino Index (ONI) data.
oni = pd.read_excel(folder_path)
oni_year = pd.DataFrame(oni['YR'])

# Seperate ONI time series into different frames.
oni_nino12 = oni['NINO12']
oni_nino3  = oni['NINO3']
oni_nino4  = oni['NINO4']
oni_nino34 = oni['NINO34']

# Create the state space models.
ssm_nino12 = sm.tsa.UnobservedComponents(oni_nino12,level='fixed intercept',freq_seasonal=[{'period': 12}],cycle=True,stochastic_cycle=True)
ssm_nino3  = sm.tsa.UnobservedComponents(oni_nino3,level='fixed intercept',freq_seasonal=[{'period': 12}],cycle=True,stochastic_cycle=True)
ssm_nino4  = sm.tsa.UnobservedComponents(oni_nino4,level='fixed intercept',freq_seasonal=[{'period': 12}],cycle=True,stochastic_cycle=True)
ssm_nino34 = sm.tsa.UnobservedComponents(oni_nino34,level='fixed intercept',freq_seasonal=[{'period':12}],cycle=True,stochastic_cycle=True)

# Estimate the state space models.
ssm_fit_nino12 = ssm_nino12.fit()
ssm_fit_nino3 = ssm_nino3.fit()
ssm_fit_nino4 = ssm_nino4.fit()
ssm_fit_nino34 = ssm_nino34.fit()

# Plot the state space model components.
figure_nino12 = ssm_fit_nino12.plot_components(observed=False)
figure_nino3 = ssm_fit_nino3.plot_components(observed=False)
figure_nino4 = ssm_fit_nino4.plot_components(observed=False)
figure_nino34 = ssm_fit_nino34.plot_components(observed=False)
plt.show()

# Predict ONI for the next year.
pred_size = 6
ssm_pred_nino12 = ssm_fit_nino12.predict(1,len(oni_nino12)+pred_size)
ssm_pred_nino3  = ssm_fit_nino3.predict(1,len(oni_nino3)+pred_size)
ssm_pred_nino4  = ssm_fit_nino4.predict(1,len(oni_nino4)+pred_size)
ssm_pred_nino34 = ssm_fit_nino34.predict(1,len(oni_nino34)+pred_size)

# Create index numbering.
oni_index = np.ones(len(ssm_pred_nino12))

# Extract start year.
start_year = oni_year.loc[0,'YR']

oni_index[0] = start_year

for i in range(0,len(ssm_pred_nino12)-1):
    
    # Check if year should be shifted.
    if ((i+1) % 12 == 0):
        
        start_year += 1
        
    # Add year to index.
    oni_index[i+1] = start_year
    
# Compute out-of-sample forecast errors.
error_nino12 = (oni_nino12 - ssm_pred_nino12) / ssm_pred_nino12
error_nino3  = (oni_nino3 - ssm_pred_nino3) / ssm_pred_nino3
error_nino4  = (oni_nino4 - ssm_pred_nino4) / ssm_pred_nino4
error_nino34 = (oni_nino34 - ssm_pred_nino34) / ssm_pred_nino34

# Remove initilization prediction.
error_nino12 = error_nino12[15:].dropna()
error_nino3 = error_nino3[15:].dropna()
error_nino4 = error_nino4[15:].dropna()
error_nino34 = error_nino34[15:].dropna()

# Average prediction errors.
MAE_nino12 = error_nino12.mean()*100
MAE_nino3 = error_nino3.mean()*100
MAE_nino4 = error_nino4.mean()*100
MAE_nino34 = error_nino34.mean()*100

# Plot the predictions for next year.
plt.plot(oni_nino12,color='blue',label='realized')
plt.plot(ssm_pred_nino12[15:len(ssm_pred_nino12)-pred_size],color='black',label='1-step historical')
plt.plot(ssm_pred_nino12[len(ssm_pred_nino12)-pred_size:],color='red',label='1-step future')
plt.title("Nino 1+2")
plt.legend()
plt.show()
plt.plot(oni_nino3,color='blue',label='realized')
plt.plot(ssm_pred_nino3[15:len(ssm_pred_nino12)-pred_size],color='black',label='1-step historical')
plt.plot(ssm_pred_nino3[len(ssm_pred_nino12)-pred_size:],color='red',label='1-step future')
plt.legend()
plt.title("Nino 3")
plt.show()
plt.plot(oni_nino4,color='blue',label='realized')
plt.plot(ssm_pred_nino4[15:len(ssm_pred_nino12)-pred_size],color='black',label='1-step historical')
plt.plot(ssm_pred_nino4[len(ssm_pred_nino12)-pred_size:],color='red',label='1-step future')
plt.legend()
plt.title("Nino 4")
plt.show()
plt.plot(oni_nino34,color='blue',label='realized')
plt.plot(ssm_pred_nino34[15:len(ssm_pred_nino12)-pred_size],color='black',label='1-step historical')
plt.plot(ssm_pred_nino34[len(ssm_pred_nino12)-pred_size:],color='red',label='1-step future')
plt.legend()
plt.title("Nino 3.4")
plt.show()

# Plot zoomed graph.
plt.plot(oni_nino12[len(ssm_pred_nino12)-pred_size-120:len(ssm_pred_nino12)-pred_size],color='blue',label='realized')
plt.plot(ssm_pred_nino12[len(ssm_pred_nino12)-pred_size-120:],color='black',label='1-step forecast')
plt.title("Nino 1+2")
plt.legend()
plt.show()

# Plot zoomed graph.
plt.plot(oni_nino3[len(ssm_pred_nino3)-pred_size-120:len(ssm_pred_nino3)-pred_size],color='blue',label='realized')
plt.plot(ssm_pred_nino3[len(ssm_pred_nino3)-pred_size-120:],color='black',label='1-step forecast')
plt.title("Nino 3")
plt.legend()
plt.show()

# Plot zoomed graph.
plt.plot(oni_nino4.iloc[len(ssm_pred_nino4)-pred_size-120:len(ssm_pred_nino3)-pred_size],color='blue',label='realized')
plt.plot(ssm_pred_nino4.iloc[len(ssm_pred_nino4)-pred_size-120:],color='black',label='1-step forecast')
plt.title("Nino 4")
plt.legend()
plt.show()

# Plot zoomed graph.
plt.plot(oni_nino34[len(ssm_pred_nino34)-pred_size-120:len(ssm_pred_nino3)-pred_size],color='blue',label='realized')
plt.plot(ssm_pred_nino34[len(ssm_pred_nino34)-pred_size-120:],color='black',label='1-step forecast')
plt.title("Nino 3.4")
plt.legend()
plt.show()