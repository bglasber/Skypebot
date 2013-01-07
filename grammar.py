def pluralize(word):
    word = word.strip()

    #irregular cases
    if word == "goose":
        word = "geese"

    elif word == "mouse":
        word = "mice"

    elif word == "knife"
        word = "knives"

    elif word == "wolf"
        word = "wolves"
        
    else:
        if word.endswith("y"):
            word = word[:-1] + "ies"

        if word.endswith("s"):
            word = word[:-1] + "es"
            
        else:
            word += "s"
        
    return word
