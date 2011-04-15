//  Copyright 2011 Jonathan Steele
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.itnoles.shared;

/**
 * Callback interface for a client to interact with the subclass for asynctask.
 * @param <T1> any object
 * @author Jonathan Steele
 */
public interface AsyncTaskCompleteListener<T1>
{
   /**
	* It will called when AsyncTask finished loading data.
	* @param result any object
	*/
    void onTaskComplete(T1 result);
}

