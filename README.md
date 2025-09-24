# Car Rental Webservice

This project is a car rental webservice built with Java and Spring Boot. It provides a RESTful API for managing cars, users, and rentals. The project also features user authentication, API documentation with Swagger, and a currency conversion service.

## Features

*   **RESTful API:** A complete API for managing cars, users, and rentals.
*   **User Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
*   **API Documentation:** Interactive API documentation using Swagger UI.
*   **Currency Conversion:** Integration with a SOAP-based currency conversion service.
*   **In-memory Database:** Uses H2 as an in-memory database for easy setup and testing.
*   **Admin Dashboard:** A simple admin dashboard built with Thymeleaf.

## Technologies Used

*   **Java 11**
*   **Spring Boot:**
    *   Spring Web
    *   Spring Data JPA
    *   Spring Security
    *   Spring HATEOAS
*   **Maven**
*   **H2 Database**
*   **Swagger 2**
*   **Lombok**
*   **Thymeleaf**

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Java JDK 11 or later
*   Maven 3.2 or later

### Installation & Running

1.  **Clone the repo**
    ```sh
    git clone https://github.com/your_username/car-rental-webservice.git
    ```
2.  **Navigate to the project directory**
    ```sh
    cd car-rental-webservice
    ```
3.  **Run the application**
    ```sh
    mvn spring-boot:run
    ```

The application will be running at `http://localhost:8080`.

## API Endpoints

Once the application is running, you can access the interactive API documentation via Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

This interface provides detailed information about all the available endpoints and allows you to test them directly in your browser.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

Please refer to the `CONTRIBUTING.md` file for more information.
