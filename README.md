## Description

This project is a demonstration of integrating **Spring AI** capabilities within a Spring-based application.

## Features

- ### Prompt 
  The **Prompt** allows to input dynamic prompts or messages that are processed to generate relevant AI-driven responses. 

- ### Prompt stuffing
  **Prompt stuffing** involves combining multiple pieces of information into a single prompt to provide the AI with richer 
context for generating more accurate and relevant responses.

- ### Retrieval-Augmented Generation (RAG)
  **RAG** combines the power of information retrieval with AI-generated responses to enhance relevance of outputs. By 
integrating a vector database, the AI can retrieve relevant context or knowledge and seamlessly incorporate it into 
responses.

- ### Functions
  The **functions** feature enables structured interactions by allowing developers to define specific functions that 
the AI can call during a conversation.

- ### Audio Text-to-Speech (TTS)
  Converts user-provided text into natural-sounding audio.

- ### Image generation
  Allows users to input descriptive prompts, generating unique and custom AI-driven images dynamically.

- ### Multi AI model integration 
  Showcases the use of multiple AI models - **OpenAI** and **Ollama** - within a single application.

## Setup and Usage
### Prerequisites

Before you begin, ensure you have met the following requirements
- `Java Development Kit (JDK) version 21` or later
- `openai.key` **(mandatory)** - is a key used to authenticate requests to OpenAI's API
- `service.weather.open-weather.api-key` **(optional)** - is a key that stores the unique API key required to access data from the
  OpenWeatherMap API.

To generate and obtain `openai.key` API key:
1. Visit [OpenAI's website](https://platform.openai.com/) and log in with your credentials. If you don’t have an account, create one by signing up.
2. Navigate to your account settings by clicking on your profile icon in the top right corner. Select **"API Keys"** from the dropdown menu or go directly to the [API Keys page](https://platform.openai.com/account/api-keys).
3. Click on the **"Create new secret key"** button. A dialog box will appear with your newly generated API key. **Copy this key immediately**, as you will not be able to view it again later.
4. Add the copied API key to configuration as required

To generate and obtain `service.weather.open-weather.api-key` API key:
1. Visit the OpenWeatherMap website at https://openweathermap.org/
2. Create an account or log in if you already have an account
3. On successful login, navigate to the `My API Keys` menu in the account settings.
4. You will see your unique API Key, or you can generate a new one if you prefer. This key is alphanumeric.

However, if `service.weather.open-weather.api-key` is not provided or invalid, the weather service has fail-safe
mechanism implemented. It will fall back to use mock data for weather information. This mock data is stored in a file
named `open-weather-mock.json`, which is part of application's resources. This file contains pre-set, static weather
data.