const stockName = document.getElementById("stockName");
const ul = document.getElementById("ul"); 
const tbody = document.getElementById("tbody");
const trashBtn = document.getElementById("trashBtn");
const actionBtn = document.getElementById("actionBtn");
const all = document.getElementById("all");
const available = document.getElementById("available");
const missing = document.getElementById("missing");
const register = document.getElementById("register");
const urlParam = new URLSearchParams(window.location.search);
const deleteConfirm = document.getElementById("deleteConfirm");
const myModal = new bootstrap.Modal(document.getElementById('myModal'));
const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
const searchByNameField = document.getElementById("searchByNameField");

/* 

TODO: Implementar modal ao clickar no item;

*/

searchByNameField.addEventListener("input", function() {
    searchByName(searchByNameField.value);
})

all.addEventListener("click", function () {
    select(all, available, missing);
    refresh("all");
});

available.addEventListener("click", function () {
    select(available, all, missing);
    refresh("available");
});

missing.addEventListener("click", function () {
    select(missing, available, all)
    refresh("missing");
});

document.addEventListener("DOMContentLoaded", function() {
    refresh("all")
});

trashBtn.addEventListener("click", function() {
    deleteModal.show()
});

deleteConfirm.addEventListener("click", function() {
    fet
})

register.addEventListener("click", function() {
    let c1 = document.getElementById("name");
    let c2 = document.getElementById("description");
    let c3 = document.getElementById("unitType");
    let c4 = document.getElementById("amount");
    let object = {
        name:c1.value,
        description:c2.value,
        unitType:c3.value,
        amount:c4.value
    }
    fetch("http://localhost:8080/stock-group/addItem/" + urlParam.get("id"),
    {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify(object)
    })
    .then(function (res) {
        myModal.hide()


        if(all.classList.contains("select")) {
            refresh("all");
        } else if(available.classList.contains("select")) {
            refresh("available");
        } else if(missing.classList.contains("select")) {
            refresh("missing");
        }        
    })
    .catch(function (res) { console.log(res) })


});

actionBtn.addEventListener("click", function() {
    let c1 = document.getElementById("name");
    let c2 = document.getElementById("description");
    let c3 = document.getElementById("unitType");
    let c4 = document.getElementById("amount");
    c1.value = "";
    c2.value = "";
    c3.value = "";
    c4.value = "";

    myModal.show();
})

function select(field, neighboringField1, neighboringField2) {
    if (field.classList.contains("select")) {
        return;
    } else {
        field.classList.add("select");
        field.classList.remove("unselect");
        neighboringField1.classList.remove("select");
        neighboringField1.classList.add("unselect");
        neighboringField2.classList.remove("select");
        neighboringField2.classList.add("unselect");
    }
}

function constructLateralBarAndHeader(object) {
    while (ul.firstChild) {
        ul.removeChild(ul.firstChild);
    }

    stockName.textContent = object.name;
    
    let d = document.createElement("div");
    d.classList = "text-center";
    let i = document.createElement("i");
    i.classList = "bi bi-collection text-white fs-4 mx-3";
    let text = document.createElement("span");
    text.textContent = "Itens";
    text.classList = "text-white h2 mb-5 mt-3 text-center"
    d.appendChild(i)
    d.appendChild(text)
    ul.appendChild(d);

    for (let i = 0; i < object.items.length; i++) {
        
        let li = document.createElement("li");
        li.classList = "p-1 ms-3 my-3 entitys d-block";
        let aa = document.createElement("a");
        aa.href = "";
        aa.textContent = object.items[i].name;
        aa.classList = "link-light fst-italic d-block";
        li.appendChild(aa);
        ul.appendChild(li);
    }
    
}

function constructTableItems(itemsArr) {
    while (tbody.firstChild) {
        tbody.removeChild(tbody.firstChild);
    }

    let td1 = document.createElement("td");
    td1.textContent = "Selecionar";
    td1.classList = "text-muted";
    td1.style.fontSize = "14px";
    
    let td2 = document.createElement("td");
    td2.textContent = "Nome";
    td2.classList = "text-muted";
    td2.style.fontSize = "14px";
    
    let td3 = document.createElement("td");
    td3.textContent = "Quantidade";
    td3.classList = "text-muted";
    td3.style.fontSize = "14px";
    
    let trHeader = document.createElement("tr");
    trHeader.classList = "text-center";
    trHeader.appendChild(td1);
    trHeader.appendChild(td2);
    trHeader.appendChild(td3);
    tbody.appendChild(trHeader);       
    
    for (let i = 0; i < itemsArr.length; i++) {
        let item = itemsArr[i];
        
        let tdCheckbox = document.createElement("td");
            tdCheckbox.classList.add("text-center");
            let inp = document.createElement("input");
            inp.value = item.id;
            inp.type = "checkbox";
            tdCheckbox.appendChild(inp);
        
        
        let tdItemName = document.createElement("td");
            tdItemName.classList.add("fs-5");
            let a = document.createElement("a");
            a.textContent = item.name;
            a.href = "";
            tdItemName.appendChild(a);
        
        
        let tdAmount = document.createElement("td");
            tdAmount.classList.add("fs-5");
            tdAmount.textContent = item.amount;
            tdAmount.classList = "text-muted";
        
        let trBody = document.createElement("tr");
        
        trBody.appendChild(tdCheckbox);
        trBody.appendChild(tdItemName);
        trBody.appendChild(tdAmount);

        tbody.appendChild(trBody);
    }
    let checkboxes = document.querySelectorAll("input[type='checkbox']")
    checkboxes.forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                trashBtn.classList.remove("disabled");
                trashBtn.classList.add("btn-danger");
                trashBtn.classList.add("text-white");

                checkboxes.forEach(function(otherCheckbox) {
                    if (otherCheckbox !== checkbox) {
                        otherCheckbox.checked = false;
                    }
                });
            } else {
                trashBtn.classList.add("disabled");
                trashBtn.classList.remove("btn-danger");
                trashBtn.classList.remove("text-white");
            }
        });
    });
}


function refresh(opt) {
    fetch("http://localhost:8080/stock-group/" + urlParam.get("id"))
    .then((response) => {
        if (!response.ok) {
            throw new Error("Erro na resposta: " + response.status);
        }
        return response.json();
    })
    .then((data) => {
        constructLateralBarAndHeader(data);
        if(opt === "all") {
            constructTableItems(data.items)
        } else if (opt === "available") {
            constructTableItems(workItems("available", data.items));
        } else if (opt === "missing") {
            constructTableItems(workItems("missing", data.items));        
        }
    })
}

function workItems(opt, arr) {
    let returnArr = [];
    if(opt === "available") {
        for (let i = 0; i < arr.length; i++) {
            let item = arr[i];
            if(item.amount >= 1) {
                returnArr.push(item);
            }            
        }
    } else if(opt === "missing") {
        for (let i = 0; i < arr.length; i++) {
            let item = arr[i];
            if(item.amount < 1) {
                returnArr.push(item);
            }            
        }
    }

    return returnArr;
}

function searchByName(string) {

    if (string.trim() !== "") {
        fetch("http://localhost:8080/item/i/" + string)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Erro na resposta: " + response.status);
                }
                return response.json();
            })
            .then((data) => {
                if(all.classList.contains("select")) {
                    constructTableItems(data);
                } else if(available.classList.contains("select")) {
                    constructTableItems(workItems("available", data));
                } else if(missing.classList.contains("select")) {
                    constructTableItems(workItems("missing", data));
                }
            })
            .catch((error) => {
                console.error('Erro ao fazer a solicitação:', error);
            });
    } else {
        if(all.classList.contains("select")) {
            refresh("all");
        } else if(available.classList.contains("select")) {
            refresh("available");
        } else if(missing.classList.contains("select")) {
            refresh("missing");
        }
    }
}