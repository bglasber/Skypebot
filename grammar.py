class Grammar:
    
    # noun contexts
    def pluralize(word):
        word = word.strip()
    
        # irregular cases
        if word == "goose":
            word = "geese"
    
        elif word == "mouse":
            word = "mice"
    
        elif word == "sheep"
            word = "sheep"
    
        elif word == "moose"
            word = "moose"
    
        elif word == "man"
            word = "men"
    
        elif word == "woman"
            word = "women"
    
        elif word == "person"
            word = "people"
            
        else:
            if word.endswith("y"):
                word = word[:-1] + "ies"
    
            if word.endswith("s"):
                word = word[:-1] + "es"
                
            if word.endswith("f"):
                word = word[:-1] + "ves"
    
            if word.endswith("fe"):
                word = word[:-2] + "ves"
                
            if word.endswith("us"):
                word = word[:-2] + "i"
                
            else:
                word += "s"
            
        return word
        
    
    
    # verb contexts
    def ing(word):
        word = word.strip()
        
        # irregular cases
        if word = "":
            word = ""
            
        else:
            if word.endswith("ie"):
                word = word[:-2] + "ying"
            
            if word.endswith("ot"):
                word = word[:-2] + "eting"
                
            else:
                word += "ing"
        
        return word
