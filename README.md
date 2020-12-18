# Newspapers desktop application

## Work done & TODO :

**Main window - ( NewsReader.fxml )**

---

- [x] List articles from server.
- [X] Filter by category
- [x] Display title, thumbnail and abstract when one article selected
- [ ] Menu : 
  - [X] Load article from file button
  - [X] Login button
  - [X] New article button
  - [X] Edit selected article button
  - [X] Delete selected article button
  - [ ] Exit / Logout button
- [x] Go to articles details button

**Article details window - ( ArticleDetails.fxml )**

---

- [x] Article image
- [x] Title
- [X] Subtitle
- [X] Category
- [X] Abstract ( html )
- [X] Body ( html )
- [x] Back button

**Article creating / editing window ( ArticleEdit.fxml )**

---

**Form to edit :**

- [X] Article image
- [X] Subtitle
- [X] Category
- [X] Abstract ( html )
- [X] Body ( html )
- [X] Back button which cancel everything
- [X] Save article to a file
- [X] Send article to server

**Form to create :**

- [X] Article image
- [X] Title
- [X] Subtitle
- [X] Category
- [X] Abstract ( html )
- [X] Body ( html )
- [X] Back button which cancel everything
- [X] Save a draft article to a file
- [X] Send article to server

---

**Login form :**

- [X] Username input
- [X] Password input
- [X] Login button


---

**17/12/2020 TODO :**

- [X] Replace Login button by Quit when logged
- [X] Gray out Read article / Edit article / Delete article if no article selected
- [X] Handle exception when "cancel" load article feature
- [X] Prevent title edition in article edition
- [X] Return to main page after creating / editing article
- [X] Add a placeholder picture when creating article
- [X] Gray out send article to server when creating article and not logged in
- [X] Add feedback if wrong credentials when trying to log in
- [ ] Edit abstract and body in HTML or plain text ( only HTML for the moment )